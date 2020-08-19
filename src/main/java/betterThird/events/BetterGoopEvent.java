package betterThird.events;

import betterThird.BetterThird;
import betterThird.relics.SlimedRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.BagOfPreparation;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.List;

public class BetterGoopEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterGoop");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/goopPuddle.jpg";

    private static final String GOLD_DIALOG, LEAVE_DIALOG, RELIC_DIALOG, BAG_INTRO;
    private CurScreen screen;
    private int damage;
    private int gold;
    private int goldLoss;
    private boolean bag;

    public BetterGoopEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CurScreen.INTRO;
        this.damage = 11;
        this.gold = 75;
        this.bag = AbstractDungeon.player.hasRelic(BagOfPreparation.ID);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldLoss = AbstractDungeon.miscRng.random(35, 75);
        } else {
            this.goldLoss = AbstractDungeon.miscRng.random(20, 50);
        }

        if(this.bag){
            this.goldLoss = 0;
            this.body = BAG_INTRO;
        }

        if (this.goldLoss > AbstractDungeon.player.gold) {
            this.goldLoss = AbstractDungeon.player.gold;
        }

        this.imageEventText.setDialogOption(OPTIONS[0] + this.gold + OPTIONS[1] + this.damage + OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[6] + this.goldLoss + OPTIONS[4] + OPTIONS[7] + this.damage + OPTIONS[8], new SlimedRelic());
        this.imageEventText.setDialogOption(OPTIONS[3] + this.goldLoss + OPTIONS[4]);

    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SPIRITS");
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                switch(buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(GOLD_DIALOG);
                        this.imageEventText.clearAllDialogs();
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.damage));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.gold));
                        AbstractDungeon.player.gainGold(this.gold);
                        this.imageEventText.setDialogOption(OPTIONS[5]);
                        this.screen = CurScreen.RESULT;
                        logMetric(ID, "Gold", null, null, null,
                                null, null, null, null,
                                this.damage, 0, 0, bag?1:0, this.gold, 0);
                        return;
                    case 1:
                        this.imageEventText.updateBodyText(RELIC_DIALOG);
                        AbstractDungeon.player.loseGold(this.goldLoss);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.damage));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), new SlimedRelic());
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[5]);
                        this.screen = CurScreen.RESULT;
                        logMetric(ID, "Slimed", null, null, null,
                                null, null, null, null,
                                this.damage, 0, 0, bag?1:0, 0, this.goldLoss);
                        return;
                    case 2:
                        this.imageEventText.updateBodyText(LEAVE_DIALOG);
                        AbstractDungeon.player.loseGold(this.goldLoss);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[5]);
                        this.screen = CurScreen.RESULT;
                        logMetric(ID, "Left", null, null, null,
                                null, null, null, null,
                                0, 0, 0, bag?1:0, 0, this.goldLoss);
                        return;
                    default:
                        return;
                }
            default:
                this.openMap();
        }
    }

    static {
        GOLD_DIALOG = DESCRIPTIONS[1];
        LEAVE_DIALOG = DESCRIPTIONS[2];
        RELIC_DIALOG = DESCRIPTIONS[3];
        BAG_INTRO = DESCRIPTIONS[4];
    }

    private enum CurScreen {
        INTRO,
        RESULT;

        CurScreen() {
        }
    }
}
