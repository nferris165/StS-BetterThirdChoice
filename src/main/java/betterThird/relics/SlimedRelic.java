package betterThird.relics;

import basemod.abstracts.CustomRelic;
import betterThird.BetterThird;
import betterThird.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import static betterThird.BetterThird.makeRelicOutlinePath;
import static betterThird.BetterThird.makeRelicPath;

public class SlimedRelic extends CustomRelic {

    public static final String ID = BetterThird.makeID("SlimedRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("Slimed.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("Slimed.png"));


    public SlimedRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);

        this.counter = -1;
    }

    @Override
    public void atBattleStart() {
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Slimed(), 1));
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + new Slimed().name + DESCRIPTIONS[1];
    }

    @Override
    public void updateDescription(AbstractPlayer.PlayerClass c) {

        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }
}
