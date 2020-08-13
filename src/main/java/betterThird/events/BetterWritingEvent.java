package betterThird.events;

import betterThird.BetterThird;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BetterWritingEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterWriting");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/backToBasics.jpg";

    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private CUR_SCREEN screen;
    private String optionsChosen;
    private List<String> cardsUpgraded;


    public BetterWritingEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.optionsChosen = "";
        this.screen = CUR_SCREEN.INTRO;
        this.cardsUpgraded = new ArrayList<>();// 32
        this.imageEventText.setDialogOption(OPTIONS[0]);// 40
        this.imageEventText.setDialogOption(OPTIONS[1]);// 41
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_ANCIENT");
        }

        this.cardsUpgraded.clear();
    }

    @Override
    public void update() {
        super.update();// 54
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {// 57
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);// 58
            AbstractDungeon.effectList.add(new PurgeCardEffect(c));// 59
            AbstractEvent.logMetricCardRemoval("Back to Basics", "Elegance", c);// 60
            AbstractDungeon.player.masterDeck.removeCard(c);// 61
            AbstractDungeon.gridSelectScreen.selectedCards.remove(c);// 62
        }

    }// 64

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {// 68
            case INTRO:
                if (buttonPressed == 0) {// 71
                    if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {// 72 73
                        this.imageEventText.updateBodyText(DIALOG_2);// 74
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()),
                                1, OPTIONS[2], false);
                    }

                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);// 82
                    this.imageEventText.clearRemainingOptions();// 83
                } else {
                    this.imageEventText.updateBodyText(DIALOG_3);// 85
                    this.upgradeStrikeAndDefends();// 86
                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);// 87
                    this.imageEventText.clearRemainingOptions();// 88
                }

                this.screen = CUR_SCREEN.COMPLETE;// 90
                break;// 91
            case COMPLETE:
                this.openMap();
        }

    }// 96

    private void upgradeStrikeAndDefends() {
        Iterator var1 = AbstractDungeon.player.masterDeck.group.iterator();

        while(true) {
            AbstractCard c;
            do {
                if (!var1.hasNext()) {
                    AbstractEvent.logMetricUpgradeCards("Back to Basics", "Simplicity", this.cardsUpgraded);
                    return;
                }

                c = (AbstractCard)var1.next();
            } while(!c.hasTag(AbstractCard.CardTags.STARTER_DEFEND) && !c.hasTag(AbstractCard.CardTags.STARTER_STRIKE));

            if (c.canUpgrade()) {
                c.upgrade();
                this.cardsUpgraded.add(c.cardID);
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), MathUtils.random(0.1F, 0.9F) * (float)Settings.WIDTH,
                        MathUtils.random(0.2F, 0.8F) * (float)Settings.HEIGHT));
            }
        }
    }

    static {
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
    }

    private enum CUR_SCREEN {
        INTRO,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
