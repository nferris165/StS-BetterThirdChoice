package betterThird.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PortalInfo implements Comparable<PortalInfo> {
    private static final Logger logger = LogManager.getLogger(PortalInfo.class.getName());
    public String name;
    public float weight;

    public PortalInfo(String name, float weight) {
        this.name = name;
        this.weight = weight;
    }

    public static void normalizeWeights(ArrayList<PortalInfo> list) {
        Collections.sort(list);
        float total = 0.0F;

        for(PortalInfo info: list){
            total += info.weight;
        }

        for(PortalInfo info: list){
            info.weight /= total;
            //logger.info(info.name + ": " + info.weight + "%");

        }
    }

    public static String roll(ArrayList<PortalInfo> list, float roll) {
        float currentWeight = 0.0F;
        Iterator var3 = list.iterator();

        PortalInfo i;
        do {
            if (!var3.hasNext()) {
                return "ERROR";
            }

            i = (PortalInfo)var3.next();
            currentWeight += i.weight;
        } while(roll >= currentWeight);

        logger.info(i.name + "\n");
        return i.name;
    }

    @Override
    public int compareTo(PortalInfo o) {
        return Float.compare(this.weight, o.weight);
    }
}
