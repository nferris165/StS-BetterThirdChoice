package betterThird.events;

import betterThird.BetterThird;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.*;

public class BetterShiningEvent extends AbstractImageEvent {
    public static final String ID = BetterThird.makeID("BetterShining");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/shiningLight.jpg";

    private static final String AGREE_DIALOG;
    private static final String DISAGREE_DIALOG;
    private int damage = 0;
    private static final float HP_LOSS_PERCENT = 0.2F;
    private static final float A_2_HP_LOSS_PERCENT = 0.3F;
    private CUR_SCREEN screen;
    private String optionsChosen;

    public BetterShiningEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";
        this.screen = CUR_SCREEN.INTRO;// 37
        if (AbstractDungeon.ascensionLevel >= 15) {// 46
            this.damage = MathUtils.round((float)AbstractDungeon.player.maxHealth * A_2_HP_LOSS_PERCENT);// 47
        } else {
            this.damage = MathUtils.round((float)AbstractDungeon.player.maxHealth * HP_LOSS_PERCENT);// 49
        }

        if (AbstractDungeon.player.masterDeck.hasUpgradableCards()) {// 52
            this.imageEventText.setDialogOption(OPTIONS[0] + this.damage + OPTIONS[1]);// 53
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3], true);// 55
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
        switch(this.screen) {// 70
            case INTRO:
                if (buttonPressed == 0) {// 72
                    this.imageEventText.updateBodyText(AGREE_DIALOG);// 73
                    this.imageEventText.removeDialogOption(1);// 74
                    this.imageEventText.updateDialogOption(0, OPTIONS[2]);// 75
                    this.screen = CUR_SCREEN.COMPLETE;// 76
                    AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.damage));// 77
                    AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));// 78
                    this.upgradeCards();// 83
                } else {
                    this.imageEventText.updateBodyText(DISAGREE_DIALOG);// 85
                    this.imageEventText.removeDialogOption(1);// 86
                    this.imageEventText.updateDialogOption(0, OPTIONS[2]);// 87
                    this.screen = CUR_SCREEN.COMPLETE;// 88
                    AbstractEvent.logMetricIgnored("Shining Light");// 89
                }
                break;
            default:
                this.openMap();// 93
        }

    }

    private void upgradeCards() {
        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));// 99
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();// 100
        Iterator var2 = AbstractDungeon.player.masterDeck.group.iterator();// 101

        while(var2.hasNext()) {
            AbstractCard c = (AbstractCard)var2.next();
            if (c.canUpgrade()) {// 102
                upgradableCards.add(c);// 103
            }
        }

        List<String> cardMetrics = new ArrayList<>();// 107
        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));// 109
        if (!upgradableCards.isEmpty()) {// 111
            if (upgradableCards.size() == 1) {// 113
                (upgradableCards.get(0)).upgrade();// 114
                cardMetrics.add((upgradableCards.get(0)).cardID);// 115
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));// 116
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect((upgradableCards.get(0)).makeStatEquivalentCopy()));// 117
            } else {
                (upgradableCards.get(0)).upgrade();// 119
                (upgradableCards.get(1)).upgrade();// 120
                cardMetrics.add((upgradableCards.get(0)).cardID);// 121
                cardMetrics.add((upgradableCards.get(1)).cardID);// 122
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));// 123
                AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));// 124
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect((upgradableCards.get(0)).makeStatEquivalentCopy(), (float)Settings.WIDTH / 2.0F - 190.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect((upgradableCards.get(1)).makeStatEquivalentCopy(), (float)Settings.WIDTH / 2.0F + 190.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
            }
        }

        AbstractEvent.logMetric("Shining Light", "Entered Light", null, null, null, cardMetrics, null, null, null, this.damage, 0, 0, 0, 0, 0);// 137
    }

    static {
        AGREE_DIALOG = DESCRIPTIONS[1];
        DISAGREE_DIALOG = DESCRIPTIONS[2];
    }

    private enum CUR_SCREEN {
        INTRO,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
