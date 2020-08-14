package betterThird.relics;

import basemod.BaseMod;
import basemod.abstracts.CustomRelic;
import betterThird.BetterThird;
import betterThird.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.RitualPower;

import static betterThird.BetterThird.makeRelicOutlinePath;
import static betterThird.BetterThird.makeRelicPath;

public class NestCultRelic extends CustomRelic {

    public static final String ID = BetterThird.makeID("NestCultRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("NestCultRelic.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("NestCultRelic.png"));

    private static final int ritual = 1;
    private static String dialog;
    public static boolean firstTurn;


    public NestCultRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.CLINK);

        firstTurn = false;
        this.counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + ritual + DESCRIPTIONS[1];
    }


    @Override
    public void atBattleStart() {
        firstTurn = false;
        this.addToBot(new MakeTempCardInHandAction(new Madness(), BaseMod.MAX_HAND_SIZE, false));
        setDialog();
        this.flash();
        this.playSfx();
        this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new RitualPower(AbstractDungeon.player, 1, true), 1));
        AbstractDungeon.actionManager.addToBottom(new TalkAction(true, dialog, 1.2F, 1.2F));
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CULTIST_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CULTIST_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CULTIST_1C"));
        }
    }

    private void setDialog(){
        int roll = MathUtils.random(2,4);
        dialog = DESCRIPTIONS[roll];
    }

    @Override
    public void updateDescription(AbstractPlayer.PlayerClass c) {

        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }
}
