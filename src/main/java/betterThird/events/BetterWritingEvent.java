package betterThird.events;

import betterThird.BetterThird;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.tempCards.Insight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.ArtOfWar;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class BetterWritingEvent extends AbstractImageEvent {

    public static final String ID = BetterThird.makeID("BetterWriting");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/backToBasics.jpg";

    private static final String DIALOG_2, DIALOG_3, DIALOG_4, DIALOG_5;
    private CUR_SCREEN screen;
    private ArrayList<AbstractCard> cardsToRemove;
    private AbstractCard card;
    private boolean watcher, defect, relic;


    public BetterWritingEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CUR_SCREEN.INTRO;
        this.card = new Insight();
        this.relic = AbstractDungeon.player.hasRelic(ArtOfWar.ID);
        if(relic){
            card.upgrade();
        }
        this.watcher = AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.WATCHER;
        this.defect = false; //AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.DEFECT;
        this.cardsToRemove = new ArrayList<>();
        this.imageEventText.setDialogOption(OPTIONS[0]);
        if(watcher){
            this.imageEventText.setDialogOption(OPTIONS[5]);
        } else if(defect){
            this.imageEventText.setDialogOption("[upgrade]");
        } else{
            this.imageEventText.setDialogOption(OPTIONS[4] + card.name + ".", card);
        }
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_ANCIENT");
        }
    }

    @Override
    public void update() {
        super.update();// 54
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.effectList.add(new PurgeCardEffect(c));
            //logMetricCardRemoval(ID, "Elegance", c);
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                if(buttonPressed == 0) {
                    if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                        this.imageEventText.updateBodyText(DIALOG_2);
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()),
                                1, OPTIONS[2], false);
                    }
                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                    this.imageEventText.clearRemainingOptions();
                } else if(buttonPressed == 1){
                    if(watcher){
                        removeStrikeAndDefends();
                        this.imageEventText.updateBodyText(DIALOG_5);
                    } else if(defect){
                        this.imageEventText.updateBodyText("get big");
                        AbstractDungeon.player.maxOrbs++;
                    } else{
                        if(relic){
                            AbstractDungeon.player.getRelic(ArtOfWar.ID).flash();
                        }
                        //logMetricObtainCardAndDamage(ID, "Insight", card, relic?1:0); // saved relic status as damage taken
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DIALOG_4);
                    }
                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                    this.imageEventText.clearRemainingOptions();
                } else if(buttonPressed == 2){
                    this.imageEventText.updateBodyText(DIALOG_3);
                    this.upgradeStrikeAndDefends();
                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                    this.imageEventText.clearRemainingOptions();
                }

                this.screen = CUR_SCREEN.COMPLETE;
                break;
            case COMPLETE:
                this.openMap();
                break;
            default:
                this.openMap();
                break;
        }

    }

    private void upgradeStrikeAndDefends() {
        Iterator var1 = AbstractDungeon.player.masterDeck.group.iterator();
        int count = 0;

        while(true) {
            AbstractCard c;
            do {
                if(!var1.hasNext()) {
                    //logMetricMaxHPLoss(ID, "Simplicity", count); //card count saved as max hp lost
                    return;
                }

                c = (AbstractCard)var1.next();
            } while(!c.hasTag(AbstractCard.CardTags.STARTER_DEFEND) && !c.hasTag(AbstractCard.CardTags.STARTER_STRIKE));

            if(c.canUpgrade()) {
                c.upgrade();
                count++;
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), MathUtils.random(0.1F, 0.9F) * (float)Settings.WIDTH,
                        MathUtils.random(0.2F, 0.8F) * (float)Settings.HEIGHT));
            }
        }
    }

    private void removeStrikeAndDefends() {
        for(AbstractCard c: AbstractDungeon.player.masterDeck.group){
            if(c.hasTag(AbstractCard.CardTags.STARTER_DEFEND) || c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)){
                cardsToRemove.add(c);
            }
        }
        //logMetricHeal(ID, "Materialism", cardsToRemove.size()); //card count saved as healing
        for(AbstractCard c: cardsToRemove){
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(),
                    MathUtils.random(0.1F, 0.9F) * (float)Settings.WIDTH,
                    MathUtils.random(0.2F, 0.8F) * (float)Settings.HEIGHT));
            AbstractDungeon.player.masterDeck.group.remove(c);
        }
    }

    static {
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        DIALOG_4 = DESCRIPTIONS[3];
        DIALOG_5 = DESCRIPTIONS[4];
    }

    private enum CUR_SCREEN {
        INTRO,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
