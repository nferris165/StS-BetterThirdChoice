package betterThird.relics;

import basemod.abstracts.CustomRelic;
import betterThird.BetterThird;
import betterThird.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.OnLoseBlockRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static betterThird.BetterThird.makeRelicOutlinePath;
import static betterThird.BetterThird.makeRelicPath;

public class SlimedRelic extends CustomRelic implements OnLoseBlockRelic {

    public static final String ID = BetterThird.makeID("SlimedRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("Slimed.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("Slimed.png"));

    private final int COUNT = 5;


    public SlimedRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);

        this.counter = 0;
    }

    @Override
    public void atBattleStart() {
        if(this.counter == COUNT){
            this.beginLongPulse();
        }
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Slimed(), 1));
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            if(this.counter == COUNT){
                this.stopPulse();
                this.flash();
                this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(info.owner, info.owner, new StrengthPower(info.owner, -1)));
                this.counter = 0;
                return 0;
            }
            else{
                this.counter++;
                if(this.counter == COUNT){
                    this.beginLongPulse();
                }
                return damageAmount;
            }

        } else {
            return damageAmount;
        }
    }

    @Override
    public int onLoseBlock(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0) {
            if(this.counter == COUNT){
                this.stopPulse();
                this.flash();
                this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(info.owner, info.owner, new StrengthPower(info.owner, -1)));
                this.counter = 0;
                return 0;
            }
            else{
                this.counter++;
                if(this.counter == COUNT){
                    this.beginLongPulse();
                }
                return damageAmount;
            }
        }
        return damageAmount;
    }

    @Override
    public void onVictory() {
        this.stopPulse();
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
