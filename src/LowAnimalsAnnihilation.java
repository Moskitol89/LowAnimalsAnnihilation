import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;

import java.awt.*;
import java.util.Objects;

@ScriptManifest(name = "Low animals annihilation", description = "Chicken Cows e.t.c. Annihilation", author = "Moskitol89",
        version = 0.1, category = Category.COMBAT, image = "")
public class LowAnimalsAnnihilation extends AbstractScript {


    private final Player PLAYER = Players.getLocal();
    private String targetName = "Chicken";
    private NPC target;
    private int food;
    private int uncookedFood;
    private Area targetArea;
    private final Area chickenArea = new Area(
            new Tile(3173, 3303, 0),
            new Tile(3169, 3299, 0),
            new Tile(3170, 3298, 0),
            new Tile(3170, 3295, 0),
            new Tile(3169, 3293, 0),
            new Tile(3169, 3291, 0),
            new Tile(3171, 3289, 0),
            new Tile(3173, 3289, 0),
            new Tile(3175, 3288, 0),
            new Tile(3178, 3289, 0),
            new Tile(3183, 3289, 0),
            new Tile(3185, 3290, 0),
            new Tile(3185, 3295, 0),
            new Tile(3186, 3296, 0),
            new Tile(3186, 3298, 0),
            new Tile(3184, 3299, 0),
            new Tile(3184, 3301, 0),
            new Tile(3182, 3302, 0),
            new Tile(3181, 3303, 0),
            new Tile(3179, 3304, 0),
            new Tile(3179, 3307, 0),
            new Tile(3173, 3307, 0));
    private final Area cowArea = new Area(
            new Tile(3193, 3300, 0),
            new Tile(3193, 3286, 0),
            new Tile(3196, 3283, 0),
            new Tile(3204, 3283, 0),
            new Tile(3207, 3284, 0),
            new Tile(3211, 3284, 0),
            new Tile(3212, 3289, 0),
            new Tile(3213, 3293, 0),
            new Tile(3210, 3298, 0),
            new Tile(3210, 3302, 0),
            new Tile(3194, 3302, 0));


    private enum STATES {
        FIGHT, MOVING, COOKING, WOODCUTTING, BANK
    }

    //Raw Chicken ; Burnt Chicken ; Cooked chicken
    //Raw beef; Burnt meat; Cooked meat   Cowhide
    private STATES state = STATES.FIGHT;

    @Override
    public void onStart() {
        Logger.log(Color.yellow, "Script started");
        //disable autologin
        getRandomManager().disableSolver(RandomEvent.LOGIN);
        if (Skill.ATTACK.getLevel() < 20) {
            targetArea = chickenArea;
            targetName = "Chicken";
        } else if (Skill.ATTACK.getLevel() >= 20) {
            targetArea = cowArea;
            targetName = "Cow";
        }
        super.onStart();
    }

    @Override
    public int onLoop() {
        state = getState();
        switch (state) {
            case FIGHT -> {
                fight();
                break;
            }
            case BANK -> {
                deposit();
                break;
            }
        }
        return Calculations.random(700, 1500);
    }

    private STATES getState() {
        if(Inventory.isFull()) {
            return STATES.BANK;
        } else return STATES.FIGHT;
    }

    private void fight() {
        log("fight()");
        if (PLAYER.isStandingStill()) {
            if(Objects.equals(targetName, "Cow")) {
                if(GroundItems.closest("Cowhide") != null) {
                    GroundItems.closest("Cowhide").interact("Take");
                    sleep(Calculations.random(1000, 1323));
                }
                if(GroundItems.closest("Bones") != null && !Inventory.isFull()) {
                    GroundItems.closest("Bones").interact("Take");
                    sleep(Calculations.random(900, 1323));
                    while (Inventory.contains("Bones")) {
                        Inventory.get("Bones").interact("Bury");
                        sleep(Calculations.random(1121, 1499));
                    }
                }
            }
            target = NPCs.closest(targetName);
            if (target == null || !target.canReach()) {
                Walking.walk(targetArea.getRandomTile());
            }
            if (target != null && !target.isInCombat()) {
                target.interact("Attack");
            }
        }
    }
    private void deposit() {
        log("deposit");
        Bank.open(Bank.getClosestBankLocation());
        Bank.depositAll("Cowhide");

    }
}