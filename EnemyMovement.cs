using UnityEngine;
using System.Collections;


	public class EnemyMovement : MonoBehaviour
	{

		Transform player;               	// Reference to the player's position.
		HealthPlayer playerHealth;    	  // Reference to the player's health.
		EnemyResetStats resetEnemyStats;
		EnemyHealth enemyHealth;        // Reference to this enemy's health.
		[System.NonSerialized]
		public NavMeshAgent nav;               // Reference to the nav mesh agent.
		public float currentEnemySpeed;
		float speed = 5f;					// speed-variable used for rotating the enemy's rotation when close to the player
		Animator anim;					// Reference to the Animator.
		Vector3 enemyInitialPos;		// Enemy's initial position. Used for calculating enemy's max fighting distance (=How far can the enemy walk from its initial position, until it goes out of combat?)
		float maxEnemyFightingRadius = 20f; //Enemy's maximum fighting distance, until out of combat.
		float enemyFightingRadius;		//Enemy's current fighting radius from its initial position.
		float distance;
		CharacterController charcontrol;
		Vector3 enemyBeginningPosition;
		bool enemyAtStartingPosition;
		Quaternion enemyInitalRotation;
		//Transform enemy; // used for calculating enemy's position.

		public bool inCombat;		//handles enemy's falling in and out of combat
		public bool evade;
		public float aggroRange = 7f;	//enemy's aggro range.
		float shoutRadius = 6f;
		int enemyMask;

		void Awake ()
		{
			// Set up the references.
			player = GameObject.FindGameObjectWithTag ("Player").transform;
			enemyMask = LayerMask.GetMask ("Shootable");

			playerHealth = player.GetComponent <HealthPlayer> ();
			enemyHealth = GetComponent <EnemyHealth> ();
			nav = GetComponent <NavMeshAgent> ();
			enemyHealth = GetComponent<EnemyHealth> ();
			anim = GetComponent <Animator> ();
			resetEnemyStats = GetComponent<EnemyResetStats> ();
		
		}

		void Start ()
		{
			//enemy's position right now
			enemyInitialPos = transform.position;
			enemyBeginningPosition = enemyInitialPos;
			enemyInitalRotation = transform.rotation;


			inCombat = false;

			//generate a random moving speed into an individual enemy to give a randomness aspect
			//nav.speed = Random.Range (2.1f, 2.4f);
		    nav.speed = 3f;
			nav.stoppingDistance = 1.9f;
			nav.radius = 0.6f;
			evade = false;
			enemyAtStartingPosition = true;
			currentEnemySpeed = nav.speed;

		}

		void Update ()
		{

			//calculates enemy's distance from another enemy
			//float enemyDistance = Vector3.Distance(enemy.position, enemyInitialPos);
			//Debug.Log (enemyDistance);

			//calculates enemy's distance from its initial position to where it's right now
			enemyFightingRadius = Vector3.Distance (transform.position, enemyInitialPos);
			//Debug.Log (enemyFightingRadius);

			//Try to determine if enemy is at its beginning transform.position. Used for resetting transfrom rotatations etc.
			if (enemyFightingRadius <= 0.25f) {
				enemyAtStartingPosition = true;
				evade = false;
			} else if (enemyFightingRadius > 0.25f) {
				enemyAtStartingPosition = false;
			}

			if (enemyAtStartingPosition && evade == false && inCombat == false) {
				//Debug.Log ("is at starting position.");
				resetEnemyStats.ResetEnemyStats ();
				transform.rotation = Quaternion.Slerp (transform.rotation, enemyInitalRotation, speed * Time.deltaTime);

			}

			//variables for rotating the enemy towards the player, when close
			distance = Vector3.Distance (player.transform.position, transform.position);
			Quaternion targetRotation = Quaternion.LookRotation (player.transform.position - transform.position);

			//Just a way to rotate the enemy's rotation towards the player, when the player gets close to the enemy, since the NavMeshAgent rotates too slow.
			if (distance < 2.5f && enemyHealth.currentHealth > 0) {
				transform.rotation = Quaternion.Slerp (transform.rotation, targetRotation, speed * Time.deltaTime);
			}

		//-----------ENEMY'S MOVEMENT TOWARDS THE PLAYER----------
		// If the enemy and the player have health left...
		if (enemyHealth.currentHealth > 0f && playerHealth.currentHealth > 0f) {
			
			// ... set the destination of the nav mesh agent to the player.
			
			//If the player goes too close to the enemy, the enemy becomes angry.
			if (distance < aggroRange && inCombat == false && evade == false) { 
				inCombat = true;
			}
			
			// war shout
			if (inCombat && enemyFightingRadius < maxEnemyFightingRadius && !evade) {
				Collider[] enemies = Physics.OverlapSphere (transform.position, shoutRadius, enemyMask);
				int i = 0;
				while (i < enemies.Length) {
					enemies [i].SendMessage ("ToArmsMen");
					i++;
				}
			}
			//Handles the animator-state
			if (inCombat || evade) {
				anim.SetBool ("Walk", true); //Running animation
			} else if (inCombat == false) {
				anim.SetBool ("Walk", false); //Idle animation
			} 
			
			//Handles the agent's movement towards the player
			if (inCombat) {
				nav.SetDestination (player.position);
			} 
			if (enemyFightingRadius > maxEnemyFightingRadius || playerHealth.currentHealth <= 0f) {
				EnemyReturn ();
			} 
		}
		// Otherwise...
		else {
			// ... disable the nav mesh agent.

			//nav.enabled = false;
		}


		}


		//Enemy's war shout. Calls nearby enemies in battle. This function could potentially be manipulated to increase stats etc...
		void ToArmsMen ()
		{	
			if (!evade)
				inCombat = true;
		}
		//If enemy goes too far from its initial position, the enemy returns to its initial position.
		void EnemyReturn ()
		{
			evade = true;
			if (evade) {
				inCombat = false;
				nav.speed = 8f;
				nav.SetDestination (enemyBeginningPosition);
			}
		}

		
	}
