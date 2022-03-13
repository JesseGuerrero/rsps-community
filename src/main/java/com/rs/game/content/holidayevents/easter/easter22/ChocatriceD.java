package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.dialogue.impl.StageSelectDialogue;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ChocatriceD extends Conversation {
    
    public ChocatriceD(Player player) {
        super(player);
        if (player.getTempAttribs().getB("talkedWithEvilChicken") && !player.getTempAttribs().getB("talkedWithChocatrice")) {
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "You already spoke to that Evil Chicken! What lies has he told you? Forget them - trust only Chocatrice!");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Chocatrice is hosting this year's Easter hunt. No more annoying bunnies or pesky, nasty little squirrels.");
            addPlayer(HeadE.CALM_TALK, "Shouldn't the Easter bunny be hosting it?");
        } else if (!player.getTempAttribs().getB("talkedWithEvilChicken") && !player.getTempAttribs().getB("talkedWithChocatrice")) {
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Good - you haven't spoken to that Evil Chicken yet. Nasty, lying chicken, that one. " +
                    "Think he's better than Chocatrice because he's made of flesh and feathers. You talk with Chocatrice; you trust Chocatrice.");
            addPlayer(HeadE.CALM_TALK, "What are you two doing here?");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Chocatrice is hosting this year's Easter hunt. No more annoying bunnies or pesky, nasty little squirrels.");
            player.getTempAttribs().setB("talkedWithChocatrice", true);
        } else if (!player.getTempAttribs().getB("talkedWithEvilChicken") && player.getTempAttribs().getB("talkedWithChocatrice")) {
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Hello again, " + player.getDisplayName());

            if (Easter2022.hasCompletedHunt(player)) {
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Well done, soldier, you've found all the eggs this hunt. You're delightfully despicable.");
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "The next hunt will be starting in " + /*timer +*/ " minutes.");
            }

            if (player.getInventory().containsItem(Easter2022.CHOCOTREAT, 3) && !player.getDiangoReclaim().contains(Easter2022.CHOCOLATE_EGG_ON_FACE_MASK)) {
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Excellent! Three delicious chocotreats for me. May I have them?");
                addOptions(new Options("buyMask", this) {
                    @Override
                    public void create() {
                        option("Yes", () -> {
                            player.getInventory().deleteItem(Easter2022.CHOCOTREAT, 3);
                            player.addDiangoReclaimItem(Easter2022.CHOCOLATE_EGG_ON_FACE_MASK);
                            player.getInventory().addItemDrop(Easter2022.CHOCOLATE_EGG_ON_FACE_MASK, 1);
                            addItem(Easter2022.CHOCOLATE_EGG_ON_FACE_MASK, "You receive the chocolate egg on face mask.").addGotoStage("huntOps", ChocatriceD.this);
                        });
                        option("No", new Dialogue()
                                .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "*bwaaak*")
                                .addGotoStage("huntOps", ChocatriceD.this));
                    }
                });
            }
            addNext(new StageSelectDialogue("huntOps", this));
        }

        addPlayer(HeadE.CALM_TALK, "Shouldn't the Easter bunny be hosting it?");
        addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Oh, you not heard? Easter Bunny had an accident. Poor Easter Bunny fell down White Wolf Mountain with a boulder tied to his feet. " +
                "Poor, poor Easter Bunny. So sad. Chocatrice saw the whole thing. Most messy.");
        addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Luckily, tragic accident happened after Easter Bunny hid his five giant, magical eggs across Runescape");

        if (player.getTempAttribs().getB("talkedWithEvilChicken")) {
            addPlayer(HeadE.CALM_TALK, "Let me guess. You want me to hunt down these eggs and blow them open with the Eggsterminator");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Oh, what has nasty Evil Chicken told you?");
        } else {
            addPlayer(HeadE.CALM_TALK, "So you want me to find these giant eggs?");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Find? Yes. They must be.... cracked... open.");
            addPlayer(HeadE.CALM_TALK, "With a giant teaspoon, right?");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, player.getDisplayName() + ", you are funny. Yes, you are... Not teaspoon. With the Eggsterminator.");
            addItem(Easter2022.EGGSTERMINATOR, "The Chocatrice shows you the Eggsterminator.");
        }

        addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Once egg smashed, little chick inside will try to escape. You shoot him down with molten chocolatey marshmallow and turn him into delicious, chocolatey treat.");
        addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Now it very important, you don't speak with Evil Chicken. He think we should turn tasty little chicks into drumsticks. Nasty, nasty Evil Chicken. " +
                "They would taste much better as chocolate, don't you agree?");

        addOptions(new Options("takingSides", this) {
               @Override
               public void create() {
                   option("Chocolate does sound better.",
                           new Dialogue().addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Yes... Delicious, scrummy, tasty chocolate.").addGotoStage("declareWar", ChocatriceD.this));
                   option("I'm more of a friend chicken kind of " + (player.getAppearance().isMale() ? "guy." : "gal."), new Dialogue()
                           .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "No! Why deep-fry when you can smother in chocolate and marshmallow? Death by chocolate!")
                           .addGotoStage("declareWar", ChocatriceD.this));
               }
        });

        create("declareWar",
        addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Evil Chicken declares war on poor Chocatrice, says he will turn more chicks into drumsticks than Chocatrice can turn into chocolate. If Evil Chicken wants war, Chocatrice gives it."));

        addOptions(new Options("huntOps", ChocatriceD.this) {
            @Override
            public void create() {
                if (!player.getInventory().containsItem(Easter2022.EGGSTERMINATOR) && !player.getBank().containsItem(Easter2022.EGGSTERMINATOR, 1) && player.getEquipment().getWeaponId() != Easter2022.EGGSTERMINATOR) {
                    option("Start the hunt.", new Dialogue()
                            .addItem(Easter2022.EGGSTERMINATOR, "You're handed (or 'winged') the Eggsterminator.")
                            .addNext(() -> {
                                player.getInventory().addItem(Easter2022.EGGSTERMINATOR, 1);
                            })
                            .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Hunt down the five eggs scattered across Runescape. Blow them open with the Eggsterminator and splatter the chick that comes from within.")
                            .addSimple("These eggs can be found around Runescape. You can search for them yourself or with your friends.") //Information can also be found on the Runescape official forums.
                            .addSimple("Finding all 5 eggs in a single hunt will unlock additional rewards.")); //Some eggs only appear in members parts of the world, so only members gain these additional benefits.
                } else {
                    option("How do I hunt the eggs?", new Dialogue()
                            .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "The Easter Bunny has hidden magical eggs across Runescape. Unfortunate, the Easter Bunny is temporarily...indisposed...and so is unable to perform his duties as huntmaster. " +
                            "I am now in charge of the hunt.")
                            .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "No, you aren't. I am!")
                            .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Quiet, you.")
                            .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Every two hours, a new hunt will begin. Hunt down the five eggs, smash them open using the Eggsterminator and then shoot at the chick that emerges with the Eggsterminator. " +
                                    "This will turn the chick into a tasty treat."));
                    option("Can I have a hint?", (Easter2022.hasFoundHintEgg(player) ?
                            new Dialogue()
                                    .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "You've already found the egg I have information on.") :
                            new Dialogue()
                                .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Nasty chicken and I agreed not to help the hunters find any of the eggs, but...")
                                .addOption("Listen to the hint?", "Yes", "No")
                                .addSimple("The bird lowers its voice.")
                                .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "One egg can be found " + Easter2022.currentEggs.get(0).getHint())));
                }
                option("Who are you?", new Dialogue()
                            .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION,"I was born from an egg dipped in chocolate. The others insulted me, even the cockatrices.")
                            .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION,"So I turned them all into chocolate! Now they no longer insult me!")
                            .addSimple("You get the impression that this bird is quite mad."));
                option("Talk about rewards.", new Dialogue().addOptions(new Options() {
                    @Override
                    public void create() {
                        option("Mask rewards", new Dialogue()
                                .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "If you gather 3 scrumptious chocotreats for me I'll trade them for a chocolate egg on face mask."));
                        option("Eggsterminator (permanent)", new Dialogue()
                                .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Complete 3 hunts to be rewarded with a permanent Eggsterminator."));
                        option("Treats and XP lamps", new Dialogue()
                                .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Every time you turn a chocochick into a chocotreat you can either eat it, or keep it. If you gather 3 scrumptious chocotreats for me I'll trade them for a chocolate egg on face mask.")
                                .addItem(Easter2022.XP_LAMP, "Players will also gain an experience lamp if they find all five eggs in a single hunt."));
                        //TODO THIS STATEMENT IS NOT GOING BACK TO HUNTOPS
                        option("Talk about something else.", new Dialogue()
                                .addGotoStage("huntOps", ChocatriceD.this));
                    }
                }));
            }
        });
        create();
    }

    public static NPCClickHandler handleChocatrice = new NPCClickHandler(new Object[] { Easter2022.CHOCATRICE }) {
        @Override
        public void handle(NPCClickEvent e) {
            if (!Easter2022.ENABLED)
                return;
            if (e.getOption().equals("Talk to")) {
                e.getPlayer().startConversation(new ChocatriceD(e.getPlayer()));
            }
        }
    };

    public static ItemOnNPCHandler handleItemOnChocatrice = new ItemOnNPCHandler(new Object[] { Easter2022.CHOCATRICE }) {
        @Override
        public void handle(ItemOnNPCEvent e) {
            if (!Easter2022.ENABLED)
                return;
            if (e.getItem().getId() == Easter2022.CHOCOLATE_EGG_ON_FACE_MASK)
                e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "In that mask, you look scrumptious. Truly delicious."));
            if (e.getItem().getId() == Easter2022.EGG_ON_FACE_MASK)
                e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "The eggs used to make that mask aren't even free-ranged. Or so I've heard."));
            if (e.getItem().getId() == Easter2022.CHOCOTREAT)
                e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Delicious."));
            if (e.getItem().getId() == Easter2022.EVIL_DRUMSTICK)
                e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Keep that fowl leg away from me."));
        }
    };
}
