using UnityEditor;
using UnityEngine;
using System.Collections.Generic;

public class MissingScriptRemover : EditorWindow
{
    private List<GameObject> objectsWithMissingScripts = new List<GameObject>();

    [MenuItem("Tools/Remove Missing Scripts")]
    public static void ShowWindow()
    {
        GetWindow<MissingScriptRemover>("Missing Script Remover");
    }

    private void OnGUI()
    {
        if (GUILayout.Button("Find Missing Scripts in Scene"))
        {
            FindMissingScripts();
        }

        if (objectsWithMissingScripts.Count > 0)
        {
            GUILayout.Label("Found " + objectsWithMissingScripts.Count + " objects with missing scripts.");

            if (GUILayout.Button("Select All Objects with Missing Scripts"))
            {
                Selection.objects = objectsWithMissingScripts.ToArray();
            }

            if (GUILayout.Button("Remove Missing Scripts from Selected Objects"))
            {
                RemoveMissingScriptsFromSelected();
            }
        }
        else
        {
            GUILayout.Label("No missing scripts found.");
        }
    }

    private void FindMissingScripts()
    {
        objectsWithMissingScripts.Clear();
        GameObject[] allObjects = FindObjectsOfType<GameObject>();
        foreach (GameObject go in allObjects)
        {
            Component[] components = go.GetComponents<Component>();
            foreach (Component c in components)
            {
                if (c == null)
                {
                    objectsWithMissingScripts.Add(go);
                    break;
                }
            }
        }
    }

    private void RemoveMissingScriptsFromSelected()
    {
        foreach (GameObject go in Selection.gameObjects)
        {
            List<Component> componentsToRemove = new List<Component>();
            Component[] components = go.GetComponents<Component>();
            foreach (Component c in components)
            {
                if (c == null)
                {
                    componentsToRemove.Add(c);
                }
            }
            foreach (Component c in componentsToRemove)
            {
                DestroyImmediate(c, true);
            }
        }
        Debug.Log("Missing scripts removed from selected objects.");
    }
}