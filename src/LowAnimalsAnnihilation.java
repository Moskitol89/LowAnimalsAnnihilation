import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;

import java.awt.*;

@ScriptManifest(name = "Low animals annihilation", description = "Chicken Cows e.t.c. Annihilation", author = "Moskitol89",
        version = 1.0, category = Category.COMBAT, image = "")
public class LowAnimalsAnnihilation extends AbstractScript {


    private final Player PLAYER = Players.getLocal();
    private String targetName = "Chicken";
    private NPC target;
    private int food;
    private int uncookedFood;
    private Condition isAnimatingEnd = new Condition() {
        @Override
        public boolean verify() {
            return !PLAYER.isAnimating() && !PLAYER.isMoving();
        }
    };

    private enum STATES {
        FIGHTING, MOVING, COOKING, WOODCUTTING
    }

    //Raw Chicken ; Burnt Chicken ; Cooked chicken
    private STATES state = STATES.FIGHTING;

    @Override
    public int onLoop() {
        food = Inventory.count("Cooked chicken");
        uncookedFood = Inventory.count("Raw chicken");
        switch (state) {
            case FIGHTING -> {
                if (food == 0 && !PLAYER.isAnimating() && !PLAYER.isMoving() && !PLAYER.isInCombat()) {
                    state = STATES.WOODCUTTING;
                }
                if (food != 0 && PLAYER.getHealthPercent() < 33) {
                    Inventory.interact("Raw Chicken", "Eat");
                    food--;
                }
                if (food < 3 && uncookedFood < 3 && !PLAYER.isInCombat()) {
                    GroundItem pickUpRawFood = GroundItems.closest("Raw Chicken");
                    if (pickUpRawFood != null) {
                        pickUpRawFood.interact("Take");
                        uncookedFood++;
                    }
                }

                target = NPCs.closest(targetName);
                if (!target.canReach()) {
                    GameObjects.closest("Gate").interact("Open");
                }
                if (!PLAYER.isInCombat()) {
                    target.interact("Attack");
                }
            }
            case WOODCUTTING -> {
                GameObject treeForFire = GameObjects.closest("Tree");
                if (!treeForFire.canReach()) {
                    GameObjects.closest("Gate").interact("Open");
                    mySleep();
                }

                if (!PLAYER.isInCombat() && !PLAYER.isAnimating() && !PLAYER.isMoving()) {
                    treeForFire.interact("Chop down");
                    mySleep();
                    Inventory.get("Tinderbox").useOn("Logs");
                    mySleep();
                    state = STATES.COOKING;
                }
            }
            case COOKING -> {
                if(uncookedFood == 0) {
                    Inventory.dropAll("Burnt Chicken");
                    state = STATES.FIGHTING;
                }
                if (!PLAYER.isAnimating()) {
                    Inventory.get("Raw Chicken").useOn(GameObjects.closest("Fire"));
                    mySleep();
                    //281 631
                    Mouse.move(new Point(281,631));
                    Mouse.click();
                    while (uncookedFood != 0) {
                    sleep(10000L, 15000L);
                    }
                    mySleep();
                    uncookedFood--;
                }
            }
        }
        return 1000;
    }

    private void mySleep() {
        sleepUntil(isAnimatingEnd,60000L,1000L);
    }
}
