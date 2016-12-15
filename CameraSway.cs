using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class CameraSway : MonoBehaviour
{
	/*Must be used with CameraSwayEditor.cs which handles the custom inspector values and functions. 
	* Remember to keep the script in question in the Editor-folder.
	*/

	// Variables to affect the sway. [] -declarations to create the slider for the variables inside the given range in the inspector.
	[Range (0f, 50)]
	public float
		intensity = 0f;
	[Range (0f, 5f)]
	public float
		magnitude = 0f;
	public Slider intensitySlid;
	public Slider magnitudeSlid;

	// Original position of the camera.
	Vector3 originalPosition;

	// Axes-values to use in the sway-effect.
	float x;
	float y;
	float z;

	void Start ()
	{
		// Store the axes when the application starts.
		x = transform.position.x;
		y = transform.position.y;
		z = transform.position.z;

		// Store the original position of the camera.
		originalPosition = new Vector3 (x, y, z);

	}
	

	void FixedUpdate ()
	{
		// Clamp the values to the given range.
		intensity = Mathf.Clamp (intensity, 0f, 50);
		magnitude = Mathf.Clamp (magnitude, 0f, 5f);

		intensity = intensitySlid.value;
		magnitude = magnitudeSlid.value;

		Earthquake ();

	}

	void Earthquake ()
	{
		// Calculate the new position for the camera.
		Vector3 newPosition = new Vector3 (x + Random.Range (-magnitude, magnitude), y + Random.Range (-magnitude, magnitude), z + Random.Range (-magnitude, magnitude));

		// Lerp the camera from its current position to the new position.
		transform.position = Vector3.Lerp (transform.position, newPosition, intensity * Time.deltaTime);
	}

	public void ResetEarthquakeVariables ()
	{
		// Reset the sway-variables and change the position of the camera to its original position.
		// This method gets called in CameraSwayEditor.cs
		if (Application.isPlaying) {
			intensity = 0f;
			magnitude = 0f;

			intensitySlid.value = 0f;
			magnitudeSlid.value = 0f;
			
			transform.position = originalPosition;
		}

	}

}
