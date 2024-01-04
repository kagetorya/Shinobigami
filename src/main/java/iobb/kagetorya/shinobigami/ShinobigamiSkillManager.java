package iobb.kagetorya.shinobigami;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import static iobb.kagetorya.shinobigami.ShinobigamiUtils.*;

public class ShinobigamiSkillManager {

    private static final String[] skilltypes = {"器術","体術","忍術","謀術","戦術","妖術"};
    private static final String[][] skills = {
            {"絡繰術","火術","水術","針術","仕込み","衣装術","縄術","登術","拷問術","壊器術","掘削術"},
            {"騎乗術","砲術","手裏剣術","手管","身体操術","歩法","走法","飛術","骨法術","刀術","怪力"},
            {"生存術","潜伏術","遁走術","盗聴術","腹話術","隠形術","変装術","香術","分身の術","隠蔽術","第六感"},
            {"医術","毒術","罠術","調査術","詐術","対人術","遊芸","九ノ一の術","傀儡の術","流言の術","経済力"},
            {"兵糧術","鳥獣術","野戦術","地の利","意気","傭兵術","記憶術","見敵術","暗号術","伝達術","人脈"},
            {"異形化","召喚術","死霊術","結界術","封術","言霊術","幻術","瞳術","千里眼の術","憑依術","呪術"}};
    private static final String[][] shortskills = {
            {"絡繰術"," 火術 "," 水術 "," 針術 ","仕込み","衣装術"," 縄術 "," 登術 ","拷問術","壊器術","掘削術"},
            {"騎乗術"," 砲術 ","手裏剣"," 手管 ","身体操"," 歩法 "," 走法 "," 飛術 ","骨法術"," 刀術 "," 怪力 "},
            {"生存術","潜伏術","遁走術","盗聴術","腹話術","隠形術","変装術"," 香術 ","分身術","隠蔽術","第六感"},
            {" 医術 "," 毒術 "," 罠術 ","調査術"," 詐術 ","対人術"," 遊芸 ","九ノ一","傀儡術","流言術","経済力"},
            {"兵糧術","鳥獣術","野戦術","地の利"," 意気 ","傭兵術","記憶術","見敵術","暗号術","伝達術"," 人脈 "},
            {"異形化","召喚術","死霊術","結界術"," 封術 ","言霊術"," 幻術 "," 瞳術 ","千里眼","憑依術"," 呪術 "}};

    public static void openSkillMenu(Player p,String id){

        TextComponent msg = new TextComponent("");

        for(int i=0;i<6;i++)
            msg.addExtra("| "+skilltypes[i]+" ");
        p.spigot().sendMessage(msg);

        for(int i=0;i<11;i++){
            msg = new TextComponent("");
            for(int j=0;j<6;j++){
                msg.addExtra("|");
                msg.addExtra(makeText(shortskills[j][i],skills[j][i],"sg charactersheet skill "+id+" "+skills[j][i]));
            }
            p.spigot().sendMessage(msg);
        }

    }
}
