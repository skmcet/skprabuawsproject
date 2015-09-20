//$Id$
package com.aws.action;

import com.test.model.Schedular;

public class StartSchedular {

    public static void schedulerStart() {
        try {
            Schedular s = new Schedular();
            System.out.println("Scheduler Thread is Starting to Run\n");
            s.run();
            System.out.println("Scheduler Thread is Started \n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
