package betterThird.events;

import betterThird.BetterThird;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Apotheosis;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.DreamCatcher;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BetterShiningEvent extends AbstractImageEvent {
    public static final String ID = BetterThird.makeID("BetterShining");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/shiningLight.jpg";

    private static final String AGREE_DIALOG, EMBRACE_DIALOG, DISAGREE_DIALOG, BURN_DIALOG, PROTECT_DIALOG;
    private int damage, burnChance, startHP, maxHP;
    private AbstractCard card, burn;
    private boolean dreamcatcher;
    private static final float HP_LOSS_PERCENT = 0.2F;
    private static final float A_2_HP_LOSS_PERCENT = 0.3F;
    private CUR_SCREEN screen;

    public BetterShiningEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.card = new Apotheosis();
        this.burn = new Burn();
        this.dreamcatcher = AbstractDungeon.player.hasRelic(DreamCatcher.ID);
        this.burnChance = 75;
        this.startHP = AbstractDungeon.player.currentHealth;
        this.maxHP = AbstractDungeon.player.maxHealth;
        this.screen = CUR_SCREEN.INTRO;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.damage = MathUtils.round((float)AbstractDungeon.player.maxHealth * A_2_HP_LOSS_PERCENT);
            this.burn.upgrade();
        } else {
            this.damage = MathUtils.round((float)AbstractDungeon.player.maxHealth * HP_LOSS_PERCENT);
            this.card.upgrade();
        }

        if (AbstractDungeon.player.masterDeck.hasUpgradableCards()) {
            this.imageEventText.setDialogOption(OPTIONS[0] + this.damage + OPTIONS[1]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3], true);
        }

        this.imageEventText.setDialogOption(OPTIONS[4] + this.card + OPTIONS[5] + this.burnChance + OPTIONS[6], this.card);
        if(dreamcatcher){
            this.imageEventText.setDialogOption(OPTIONS[7] + this.card + OPTIONS[8], this.card);
        }
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SHINING");
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    this.imageEventText.updateBodyText(AGREE_DIALOG);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[2]);
                    this.screen = CUR_SCREEN.COMPLETE;
                    AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.damage));
                    AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                    this.upgradeCards();
                } else if (buttonPressed == 1){
                    String choice = "Embrace";
                    int roll = AbstractDungeon.miscRng.random(0,99);
                    if(roll < burnChance){
                        //AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float) Settings.WIDTH * 0.4F, (float)Settings.HEIGHT / 2.0F, false));
                        //AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(burn, (float)Settings.WIDTH  * 0.6F, (float)Settings.HEIGHT / 2.0F, false));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(burn, (float) Settings.WIDTH * 0.5F, (float)Settings.HEIGHT / 2.0F, false));
                        this.imageEventText.updateBodyText(BURN_DIALOG);
                        choice += " Burn";
                    }
                    else{
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float) Settings.WIDTH * 0.5F, (float)Settings.HEIGHT / 2.0F, false));
                        this.imageEventText.updateBodyText(EMBRACE_DIALOG);
                    }
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[2]);
                    this.screen = CUR_SCREEN.COMPLETE;
                    logMetric(ID, choice, null, null, null,
                            null, null, null, null, 0,
                            startHP, maxHP, 0, 0, dreamcatcher?1:0);
                } else if (buttonPressed == 2){
                    if(dreamcatcher){
                        AbstractDungeon.player.loseRelic(AbstractDungeon.player.getRelic(DreamCatcher.ID).relicId);
                        this.imageEventText.updateBodyText(PROTECT_DIALOG + EMBRACE_DIALOG);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[2]);
                        this.screen = CUR_SCREEN.COMPLETE;
                        logMetric(ID, "Protect", null, null, null,
                                null, null, null, null, 0,
                                startHP, maxHP, 0, 0, dreamcatcher?1:0);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float) Settings.WIDTH * 0.5F, (float)Settings.HEIGHT / 2.0F, false));
                    } else{
                        this.imageEventText.updateBodyText(DISAGREE_DIALOG);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[2]);
                        this.screen = CUR_SCREEN.COMPLETE;
                        logMetric(ID, "Leave", null, null, null,
                                null, null, null, null, 0,
                                startHP, maxHP, 0, 0, dreamcatcher?1:0);
                    }
                } else if (buttonPressed == 3){
                    this.imageEventText.updateBodyText(DISAGREE_DIALOG);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[2]);
                    this.screen = CUR_SCREEN.COMPLETE;
                    logMetric(ID, "Leave", null, null, null,
                            null, null, null, null, 0,
                            startHP, maxHP, 0, 0, dreamcatcher?1:0);

                }
                break;
            default:
                this.openMap();
        }

    }

    private void upgradeCards() {
        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade()) {
                upgradableCards.add(c);
            }
        }

        List<String> cardMetrics = new ArrayList<>();
        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));
        if (!upgradableCards.isEmpty()) {
            if (upgradableCards.size() == 1) {
                (upgradableCards.get(0)).upgrade();
                cardMetrics.add((upgradableCards.get(0)).cardID);
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect((upgradableCards.get(0)).makeStatEquivalentCopy()));
            } else {
                (upgradableCards.get(0)).upgrade();
                (upgradableCards.get(1)).upgrade();
                cardMetrics.add((upgradableCards.get(0)).cardID);
                cardMetrics.add((upgradableCards.get(1)).cardID);
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect((upgradableCards.get(0)).makeStatEquivalentCopy(),
                        (float)Settings.WIDTH / 2.0F - 190.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect((upgradableCards.get(1)).makeStatEquivalentCopy(),
                        (float)Settings.WIDTH / 2.0F + 190.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
            }
        }
        logMetric(ID, "Upgrade", null, null, null,
                cardMetrics, null, null, null, this.damage,
                startHP, maxHP, 0, 0, dreamcatcher?1:0);
    }

    static {
        AGREE_DIALOG = DESCRIPTIONS[1];
        DISAGREE_DIALOG = DESCRIPTIONS[2];
        EMBRACE_DIALOG = DESCRIPTIONS[3];
        BURN_DIALOG = DESCRIPTIONS[4];
        PROTECT_DIALOG = DESCRIPTIONS[5];
    }

    private enum CUR_SCREEN {
        INTRO,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
