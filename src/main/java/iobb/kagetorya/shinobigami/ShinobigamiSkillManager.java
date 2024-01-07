package iobb.kagetorya.shinobigami;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

import static iobb.kagetorya.shinobigami.ShinobigamiTables.numbers;
import static iobb.kagetorya.shinobigami.ShinobigamiUtils.*;

public class ShinobigamiSkillManager {

    // スキルの種類の定義
    private static final String[] skillTypes = {"器術","体術","忍術","謀術","戦術","妖術"};
    private static final String[] skillTypeColors = {"§e","§6","§5","§9","§c","§d"};

    // スキルの定義
    private static final String[][] skills = {
            {"絡繰術","火術","水術","針術","仕込み","衣装術","縄術","登術","拷問術","壊器術","掘削術"},
            {"騎乗術","砲術","手裏剣術","手管","身体操術","歩法","走法","飛術","骨法術","刀術","怪力"},
            {"生存術","潜伏術","遁走術","盗聴術","腹話術","隠形術","変装術","香術","分身の術","隠蔽術","第六感"},
            {"医術","毒術","罠術","調査術","詐術","対人術","遊芸","九ノ一の術","傀儡の術","流言の術","経済力"},
            {"兵糧術","鳥獣術","野戦術","地の利","意気","用兵術","記憶術","見敵術","暗号術","伝達術","人脈"},
            {"異形化","召喚術","死霊術","結界術","封術","言霊術","幻術","瞳術","千里眼の術","憑依術","呪術"}};

    // スキル表記
    private static final String[][] skillsShorter = {
            {" 絡繰 "," 火術 "," 水術 "," 針術 "," 仕込 "," 衣装 "," 縄術 "," 登術 "," 拷問 "," 壊器 "," 掘削 "},
            {" 騎乗 "," 砲術 "," 手裏 "," 手管 "," 身操 "," 歩法 "," 走法 "," 飛術 "," 骨法 "," 刀術 "," 怪力 "},
            {" 生存 "," 潜伏 "," 遁走 "," 盗聴 "," 腹話 "," 隠形 "," 変装 "," 香術 "," 分身 "," 隠蔽 "," 六感 "},
            {" 医術 "," 毒術 "," 罠術 "," 調査 "," 詐術 "," 対人 "," 遊芸 "," 九一 "," 傀儡 "," 流言 "," 経済 "},
            {" 兵糧 "," 鳥獣 "," 野戦 "," 地利 "," 意気 "," 用兵 "," 記憶 "," 見敵 "," 暗号 "," 伝達 "," 人脈 "},
            {" 異形 "," 召喚 "," 死霊 "," 結界 "," 封術 "," 言霊 "," 幻術 "," 瞳術 "," 千眼 "," 憑依 "," 呪術 "}};

    // 各スキル説明
    private static final String[][] skillDescriptions = {
            {"機械や電子機器を上手に操る技術。","火薬や火器の取り扱い、火や熱に関する知識。","水上移動や潜水を行うための道具、液体に関する知識。","鍼灸などの技術。含み針や方位の調査にも使用できる。","衣装や日用品などの中に武器を隠す技術。","服で素性を隠したり、色々なものを収納する技術。","縄を投げたり、強く結んだする技術。捕縛術にも。","塀や壁などをうまく登るための技術。","拷問具を使って、的確に痛みを与える技術。","開器術とも。錠前や窓、機械部品などを破壊する技術。","素早く壁や地面に穴を掘る技術。"},
            {"乗物や騎乗動物をうまく乗りこなす技術。","鈍器や大砲をうまく撃つ技術。弾道学など。","十字、八方、卍など、様々な手裏剣を扱う技術。","手先の器用さ。印を組んだり、精密作業に使われる。","自分の体を操作する技術。平衡感覚を試したり、骨を外したりするときに使われる。","音を立てずに歩いたり、長距離を歩いたりできる。","素早く移動するときに使用される。","空を跳躍したり、高いところから着地するための技術。","格闘術。無手で戦うときに使用される。","刀をはじめとする白兵武器を使うための技術。","重いものを持ち上げたり、何かを運ぶときに使われる。"},
            {"高温や低温、水中、飢餓状態など極限的な状況で、生き残るための技術。","長期間、敵地や劣悪な環境に潜伏するための技術。","窮地から逃げ出すための技術。","床や天井越しに盗み聞いたり、ハッキングを行う技術。","自分から離れた場所から音を出す技術。声帯模写も含む。","自分の姿を隠すための技術。","別人になりすますための技術。","自分の体臭を消したり、匂いを調合するための技術。","まるでいくつかの体に分かれたかのように、並行して複数の行動を行うための技術。","人や物や情報を何かから隠すための技術。","感覚器官に頼らない知覚能力。経験から来る予知など。"},
            {"病気やケガを治療する技術。人体に関する知識。","人体に有害な物質に関する知識。またその運用技術。","用害の術とも。 侵入者や追跡者を罠にかける技術。","物的、人的な証拠を集め、調べる技術。","自分の本心を隠したり、人を騙すための技術。","他人への印象を操作するための技術。","絵画や音楽、踊りなど、芸能分野全般に関する技術。","色香によって人をたぶらかす技術。","人を思い通りに操るための技術。","様々な噂を流す技術。情報工作。","資金が必要だったり、何かを購入するときに使われる。"},
            {"糧食の補給や調達に関する技術。栄養価の高い食事を作ることができる。","獣や虫などを自在に操る技術。","野山で活動するための技術。","戦闘時、高低起伏、地表面土質、水系、植生、人工建築物などをうまく活用するための技術。","精神力。動揺や混乱しそうなときに使われる。","部隊を効率的に運用するための技術。","物事を覚えるための技術。","敵を発見したり、その力を分析するための技術。","暗号の解読、作成技術。合言葉や隠語に関する知識。","通信やサイン、書面を使って、うまく情報を伝えるための技術。","影の世界や表の世界での有力な人物とのつながり。"},
            {"自分の体の一部、もしくは全部を変形させる。","異世界の生き物を呼び出し操る技術。","死者の魂や肉体を操る技術。","特定の存在が立ち入ることができない空間をつくりだす技術。","妖術を封じる技術。","言葉に潜む魔力を引き出す技術。","人の目をあざむく幻を生み出す技術。","自分の見た者や、その瞳を見たものを操る技術。","遠く離れた場所や過去、未来を見通す技術。","自分の精神を誰かの肉体に移す技術。","人を呪って不幸にする技術。"}
    };

    // スキル取得画面
    public static void openSkillMenu(Player p,String id){
        // シート準備
        File sheet = getSheetPath(p,id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);

        // 行間
        p.sendMessage(" ");

        // スキルの種類一覧
        TextComponent msg = new TextComponent("");
        p.sendMessage("| —— | —— | —— | —— | —— | —— |");
        for(int i=0;i<6;i++)
            msg.addExtra("| "+skillTypeColors[i]+skillTypes[i]+" ");
        msg.addExtra("|");
        p.spigot().sendMessage(msg);
        p.sendMessage("| —— | —— | —— | —— | —— | —— |");
        List<String> playerSkills = cfg.getStringList("skills");

        // スキル一覧
        for(int i=0;i<11;i++){
            msg = new TextComponent("");
            for(int j=0;j<6;j++){
                msg.addExtra("|");
                String color;
                if(playerSkills.contains(skills[j][i]))
                    color = skillTypeColors[j];
                else
                    color = "§f";
                msg.addExtra(makeText(color+skillsShorter[j][i]+"§r",skillTypeColors[j]+skills[j][i]+"§r\n"+skillDescriptions[j][i],"/sg charactersheet skill "+id+" "+skills[j][i]));
            }
            msg.addExtra("|");
            p.spigot().sendMessage(msg);
        }
        p.sendMessage("| —— | —— | —— | —— | —— | —— |");

        // 操作類
        msg = new TextComponent("| ");
        msg.addExtra(makeText("§b更新§r","クリックで取得したスキルを更新します。","/sg gui "+id+" skill"));
        msg.addExtra(" | ");
        msg.addExtra(makeText("§c削除§r","クリックでひとつ前に取得したスキルを削除します。","/sg charactersheet skill "+id+" delete"));
        msg.addExtra(" | "+playerSkills.size()+"/"+numbers.get("rank_"+cfg.getString("rank")+"_skills")+" |");
        p.spigot().sendMessage(msg);
    }

    // スキルの編集
    public static void editSkill(Player p, String id, String skill){

        // シートの準備
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        List<String> skillList = cfg.getStringList("skills");

        // 既に取得済みのスキルを渡された場合
        if (skillList.contains(skill)){
            removeSkill(p,id,skill);
        }
        // 新規の場合
        else {
            addSkill(p,id,skill);
        }

    }

    // スキル取得処理
    public static void addSkill(Player p, String id, String skill){

        // シート準備
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        List<String> skillList = cfg.getStringList("skills");

        // 取得上限の確認
        if(checkSkill(p,id))
            return;

        // シートへの書き込み
        skillList.add(skill);
        setSheetAsList(p,id,"skills",skillList);
        openSkillMenu(p,id);
    }

    // スキル削除処理
    public static void removeSkill(Player p, String id, String skill){

        // シート準備
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        List<String> skillList = cfg.getStringList("skills");

        // 最後に取得したスキルの削除
        if(skill == null) {
            if (skillList.size() != 0) {
                skillList.remove(skillList.size() - 1);
                setSheetAsList(p, id, "skills", skillList);
            }
        }

        // スキルが指定されていた際の処理
        else {

            // スキルの探索
            boolean b = true;
            outerLoop:
            for(String[] outer : skills)
                for(String inner : outer)
                    if(inner.equalsIgnoreCase(skill)){
                        b = false;
                        break outerLoop;
                    }

            // スキル入力の例外処理
            if(b){
                p.sendMessage("§cError: スキルの入力が不正です。§8入力値:"+skill);
                return;
            }

            // スキルの削除
            skillList.remove(skill);
            setSheetAsList(p, id, "skills", skillList);
        }
        openSkillMenu(p,id);
    }

    // スキル数の確認
    public static boolean checkSkill(Player p, String id){
        // シート準備
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        List<String> skillList = cfg.getStringList("skills");


        // 所持スキル数が上限値を超過
        while(numbers.get("rank_"+cfg.getString("rank")+"_skills") < skillList.size())
            removeSkill(p,id,null);

        // 所持スキルが上限値と同値であるか y=1,n=0
        return numbers.get("rank_" + cfg.getString("rank") + "_skills") == skillList.size();
    }
}
