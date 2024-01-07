package iobb.kagetorya.shinobigami;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static iobb.kagetorya.shinobigami.ShinobigamiTables.prefix;
import static iobb.kagetorya.shinobigami.ShinobigamiUtils.getSheetPath;
import static iobb.kagetorya.shinobigami.ShinobigamiUtils.setSheet;

public class ShinobigamiPoints {
    public static void addPoint(Player p, String id, int i){
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);

        if(i < 0 && !isAbleRemovePoint(p,id,i)){
            p.sendMessage(prefix+"§c"+i+"減らすと功績点が負の数になります。");
            return;
        }

        int point = cfg.getInt("points");
        point += i;
        setSheet(p,id,"points",Integer.toString(point));
    }

    public static boolean isAbleRemovePoint(Player p, String id, int i){
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);

        int point = cfg.getInt("points");

        return point >= i;
    }
}
