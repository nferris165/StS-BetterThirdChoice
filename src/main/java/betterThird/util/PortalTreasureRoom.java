package betterThird.util;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.TreasureRoom;

public class PortalTreasureRoom extends TreasureRoom {

    @Override
    public void onPlayerEntry() {
        this.playBGM(null);
        this.chest = new PortalChest();
        AbstractDungeon.overlayMenu.proceedButton.setLabel(TEXT[0]);
    }
}
