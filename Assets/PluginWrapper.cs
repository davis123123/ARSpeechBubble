using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PluginWrapper : MonoBehaviour {

	// Use this for initialization
	void Start () {
		TextMesh textMesh = GetComponent<TextMesh> ();
		var plugin = new AndroidJavaClass ("com.google.cloud.android.speech");
		textMesh.text = plugin.CallStatic<string> ("GetTextFromPlugin", 7);
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
