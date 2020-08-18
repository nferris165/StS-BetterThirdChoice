package betterThird.events;

import betterThird.BetterThird;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.cards.green.Accuracy;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Collections;

public class BetterScrapEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterScrap");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/scrapOoze.jpg";

    private int dmg = 3;
    private int cardDmg = 3;
    private final int DEF_REQ = 10, RELIC_BASE_CHANCE = 25, CARD_BASE_CHANCE = 30;
    private int totalDamageDealt = 0;
    private int relicObtainChance, cardObtainChance;
    private AbstractCard card;
    private static final String FAIL_MSG, SUCCESS_MSG, ESCAPE_MSG, CARD_SUCCESS_MSG, SUCCESS_TEASE, FINAL_MSG;
    private boolean relic, relic2, cardEarned, cardEarned2, defense;
    private CurScreen screen;
    private String optionsChosen;

    public BetterScrapEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";
        this.relic = relic2 = false;
        this.relicObtainChance = RELIC_BASE_CHANCE;
        this.cardObtainChance = CARD_BASE_CHANCE;
        this.cardEarned = cardEarned2 = false;
        this.defense = defCard();
        this.screen = CurScreen.INTRO;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.dmg = 5;
            this.cardDmg = 4;
        }

        generateCard();

        this.imageEventText.setDialogOption(OPTIONS[0] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[0] + this.cardDmg + OPTIONS[1]
                + this.cardObtainChance + OPTIONS[5] + this.card.name + OPTIONS[6], this.card);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_OOZE");
        }

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                int random;
                switch(buttonPressed) {
                    case 0:
                        AbstractDungeon.player.damage(new DamageInfo(null, this.dmg));
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        this.totalDamageDealt += this.dmg;
                        random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.relicObtainChance) {
                            if(this.defense){
                                if(relic){
                                    if(this.cardEarned2){
                                        this.imageEventText.updateBodyText(SUCCESS_MSG + FINAL_MSG);
                                        this.imageEventText.clearAllDialogs();
                                        this.imageEventText.setDialogOption(OPTIONS[3]);
                                        this.screen = CurScreen.LEAVE;
                                    }
                                    else{
                                        this.imageEventText.updateBodyText(SUCCESS_MSG + SUCCESS_TEASE);
                                        this.relic2 = true;
                                        this.imageEventText.updateDialogOption(0, OPTIONS[7], true);
                                    }
                                    AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                                    this.optionsChosen += "Relic ";
                                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r.makeCopy());
                                } else{
                                    this.imageEventText.updateBodyText(SUCCESS_MSG + SUCCESS_TEASE);
                                    this.relic = true;
                                    this.relicObtainChance = RELIC_BASE_CHANCE;
                                    this.imageEventText.updateDialogOption(0, OPTIONS[10] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);
                                    AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                                    this.optionsChosen += "Relic ";
                                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r.makeCopy());
                                }

                            } else{
                                this.imageEventText.updateBodyText(SUCCESS_MSG + SUCCESS_TEASE);
                                if(this.cardEarned){
                                    this.imageEventText.clearAllDialogs();
                                    this.imageEventText.setDialogOption(OPTIONS[3]);
                                    this.screen = CurScreen.LEAVE;
                                }
                                else{
                                    this.relic = true;
                                    this.imageEventText.updateDialogOption(0, OPTIONS[8] + DEF_REQ + OPTIONS[9], true);
                                }
                                AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                                this.optionsChosen += "Relic ";
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, r.makeCopy());
                            }
                        } else {
                            this.imageEventText.updateBodyText(FAIL_MSG);
                            this.relicObtainChance += 10;
                            ++this.dmg;
                            this.imageEventText.updateDialogOption(0, OPTIONS[4] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);
                        }

                        return;
                    case 1:
                        AbstractDungeon.player.damage(new DamageInfo(null, this.cardDmg));
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        this.totalDamageDealt += this.cardDmg;
                        random = AbstractDungeon.miscRng.random(0, 99);
                        if (random >= 99 - this.cardObtainChance) {
                            if(this.defense){
                                if(cardEarned){
                                    if(this.relic2){
                                        this.imageEventText.updateBodyText(CARD_SUCCESS_MSG + FINAL_MSG);
                                        this.imageEventText.clearAllDialogs();
                                        this.imageEventText.setDialogOption(OPTIONS[3]);
                                        this.screen = CurScreen.LEAVE;
                                    }
                                    else{
                                        this.imageEventText.updateBodyText(CARD_SUCCESS_MSG + SUCCESS_TEASE);
                                        this.cardEarned2 = true;
                                        this.imageEventText.updateDialogOption(1, OPTIONS[7], true);
                                    }
                                    this.optionsChosen += "Card ";
                                    if(this.card.color == AbstractCard.CardColor.BLUE && AbstractDungeon.player.masterMaxOrbs == 0){
                                        AbstractDungeon.player.masterMaxOrbs = 1;
                                    }
                                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card,
                                            (float) Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                                } else{
                                    this.imageEventText.updateBodyText(CARD_SUCCESS_MSG + SUCCESS_TEASE);
                                    this.cardEarned = true;
                                    this.optionsChosen += "Card ";
                                    if(this.card.color == AbstractCard.CardColor.BLUE && AbstractDungeon.player.masterMaxOrbs == 0){
                                        AbstractDungeon.player.masterMaxOrbs = 1;
                                    }
                                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card,
                                            (float) Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                                    this.cardObtainChance = CARD_BASE_CHANCE;
                                    generateCard();
                                    this.imageEventText.updateDialogOption(1,OPTIONS[10] + this.cardDmg + OPTIONS[1]
                                            + this.cardObtainChance + OPTIONS[5] + this.card.name + OPTIONS[6], this.card);
                                }
                            } else{
                                this.imageEventText.updateBodyText(CARD_SUCCESS_MSG + SUCCESS_TEASE);
                                if(this.relic){
                                    this.imageEventText.clearAllDialogs();
                                    this.imageEventText.setDialogOption(OPTIONS[3]);
                                    this.screen = CurScreen.LEAVE;
                                }
                                else{
                                    this.cardEarned = true;
                                    this.imageEventText.updateDialogOption(1, OPTIONS[8] + DEF_REQ + OPTIONS[9], true);
                                }
                                this.optionsChosen += "Card ";
                                if(this.card.color == AbstractCard.CardColor.BLUE && AbstractDungeon.player.masterMaxOrbs == 0){
                                    AbstractDungeon.player.masterMaxOrbs = 1;
                                }
                                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card,
                                        (float) Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                            }
                        } else {
                            this.imageEventText.updateBodyText(FAIL_MSG);
                            this.cardObtainChance += 10;
                            ++this.cardDmg;
                            this.imageEventText.updateDialogOption(1,OPTIONS[4] + this.cardDmg + OPTIONS[1]
                                    + this.cardObtainChance + OPTIONS[5] + this.card.name + OPTIONS[6], this.card);
                        }

                        return;
                    case 2:
                        if(this.optionsChosen.isEmpty()){
                            this.optionsChosen = "LEAVE";
                        }
                        AbstractEvent.logMetricTakeDamage(ID, this.optionsChosen, this.totalDamageDealt);
                        this.imageEventText.updateBodyText(ESCAPE_MSG);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        this.screen = CurScreen.LEAVE;
                        return;
                    default:
                        BetterThird.logger.info("ERROR: case " + buttonPressed + " should never be called");
                        return;
                }
            case LEAVE:
                this.openMap();
        }

    }

    private void generateCard(){
        ArrayList<AbstractCard> tmpPool = new ArrayList<>();
        ArrayList<AbstractCard> cardPool = new ArrayList<>();
        ArrayList<String> exclude = addExclusions();

        switch (AbstractDungeon.player.chosenClass){
            case DEFECT:
                CardLibrary.addGreenCards(tmpPool);
                CardLibrary.addRedCards(tmpPool);
                CardLibrary.addPurpleCards(tmpPool);
                break;
            case THE_SILENT:
                CardLibrary.addBlueCards(tmpPool);
                CardLibrary.addRedCards(tmpPool);
                CardLibrary.addPurpleCards(tmpPool);
                break;
            case IRONCLAD:
                CardLibrary.addGreenCards(tmpPool);
                CardLibrary.addBlueCards(tmpPool);
                CardLibrary.addPurpleCards(tmpPool);
                break;
            case WATCHER:
                CardLibrary.addGreenCards(tmpPool);
                CardLibrary.addRedCards(tmpPool);
                CardLibrary.addBlueCards(tmpPool);
                break;
            default:
                CardLibrary.addGreenCards(tmpPool);
                CardLibrary.addRedCards(tmpPool);
                CardLibrary.addPurpleCards(tmpPool);
                CardLibrary.addBlueCards(tmpPool);
                break;
        }

        for(AbstractCard c: tmpPool){
            if(c.rarity != AbstractCard.CardRarity.COMMON){
                if(!exclude.contains(c.cardID)){
                    cardPool.add(c.makeCopy());
                }
            }
        }
        Collections.shuffle(cardPool, AbstractDungeon.miscRng.random);
        this.card = cardPool.get(0);

    }

    private boolean defCard(){
        for(AbstractCard c: AbstractDungeon.player.masterDeck.group){
            if(c.baseBlock >= DEF_REQ){
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> addExclusions(){
        ArrayList<String> exclude = new ArrayList<>();

        exclude.add(Accuracy.ID);
        exclude.add(InnerPeace.ID);
        exclude.add(Indignation.ID);
        exclude.add(Blizzard.ID);
        exclude.add(Capacitor.ID);
        exclude.add(Consume.ID);
        exclude.add(Defragment.ID);
        exclude.add(Loop.ID);
        exclude.add(BiasedCognition.ID);
        exclude.add(Fission.ID);
        exclude.add(MultiCast.ID);
        exclude.add(LikeWater.ID);
        exclude.add(MentalFortress.ID);
        exclude.add(Pray.ID);
        exclude.add(Rushdown.ID);
        exclude.add(Worship.ID);

        return exclude;

    }

    static {
        FAIL_MSG = DESCRIPTIONS[1];
        SUCCESS_MSG = DESCRIPTIONS[2];
        ESCAPE_MSG = DESCRIPTIONS[3];
        CARD_SUCCESS_MSG = DESCRIPTIONS[4];
        SUCCESS_TEASE = DESCRIPTIONS[5];
        FINAL_MSG = DESCRIPTIONS[6];
    }

    private enum CurScreen {
        INTRO,
        LEAVE;

        CurScreen() {
        }
    }
}
