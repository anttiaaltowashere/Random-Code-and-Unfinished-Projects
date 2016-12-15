using UnityEngine;
using System.Collections;
using CompleteProject;
public class EnemyResetStats : MonoBehaviour {

	//NavMeshAgent nav;
	EnemyMovement enemyMovement;
	EnemyHealth enemyHealth;
	//EnemyMovement enemyMovement;


	void Awake () {
		enemyHealth = GetComponent<EnemyHealth>();
		//nav = GetComponent<NavMeshAgent>();
		enemyMovement = GetComponent<EnemyMovement>();
		//enemyMovement = GetComponent<EnemyMovement>();
	}
	// Use this for initialization
	void Start () {
	


	}
	
	// Update is called once per frame
	void Update () {
	
	}
	public void ResetEnemyStats (){
		enemyHealth.currentHealth = enemyHealth.startingHealth;
		enemyMovement.nav.speed = enemyMovement.currentEnemySpeed;
		//add more resetable enemy stats if necessary
	}

}
