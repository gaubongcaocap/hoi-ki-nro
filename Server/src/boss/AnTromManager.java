/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boss;

/**
 *
 * @author Administrator
 */
public class AnTromManager extends BossManager {

    private static AnTromManager instance;

    public static AnTromManager gI() {
        if (instance == null) {
            instance = new AnTromManager();
        }
        return instance;
    }

}
