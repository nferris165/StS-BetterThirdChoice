package betterThird.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.potions.FairyPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.MercuryHourglass;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class PortalChest extends AbstractChest {
    public PortalChest() {
        this.img = ImageMaster.L_CHEST;
        this.openedImg = ImageMaster.L_CHEST_OPEN;
        this.hb = new Hitbox(340.0F * Settings.scale, 200.0F * Settings.scale);
        this.hb.move(CHEST_LOC_X, CHEST_LOC_Y - 120.0F * Settings.scale);
        this.goldReward = true;
        this.GOLD_AMT = 124;
    }

    @Override
    public void open(boolean bossChest) {
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.overlayMenu.proceedButton.setLabel(TEXT[0]);

        for(AbstractRelic r: AbstractDungeon.player.relics ){
            r.onChestOpen(bossChest);
        }

        CardCrawlGame.sound.play("CHEST_OPEN");
        if (this.goldReward) {
            if (Settings.isDailyRun) {
                AbstractDungeon.getCurrRoom().addGoldToRewards(this.GOLD_AMT);
            } else {
                AbstractDungeon.getCurrRoom().addGoldToRewards(Math.round(AbstractDungeon.treasureRng.random((float)this.GOLD_AMT * 0.9F, (float)this.GOLD_AMT * 1.1F)));
            }
        }

        AbstractDungeon.getCurrRoom().addPotionToRewards(new FairyPotion());
        AbstractDungeon.getCurrRoom().addRelicToRewards(new MercuryHourglass());
        AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(AbstractCard.CardColor.COLORLESS));

        if (this.cursed) {
            AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(AbstractDungeon.returnRandomCurse(), this.hb.cX, this.hb.cY));
        }

        for(AbstractRelic r: AbstractDungeon.player.relics ){
            r.onChestOpenAfter(bossChest);
        }

        AbstractDungeon.combatRewardScreen.open();
    }
}
