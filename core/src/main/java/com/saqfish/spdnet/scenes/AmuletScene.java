/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.saqfish.spdnet.scenes;

import com.saqfish.spdnet.Assets;
import com.saqfish.spdnet.Chrome;
import com.saqfish.spdnet.Dungeon;
import com.saqfish.spdnet.GamesInProgress;
import com.saqfish.spdnet.ShatteredPixelDungeon;
import com.saqfish.spdnet.effects.BadgeBanner;
import com.saqfish.spdnet.effects.Flare;
import com.saqfish.spdnet.effects.Speck;
import com.saqfish.spdnet.items.Amulet;
import com.saqfish.spdnet.messages.Messages;
import com.saqfish.spdnet.net.Sender;
import com.saqfish.spdnet.net.ui.NetIcons;
import com.saqfish.spdnet.net.windows.WndNetOptions;
import com.saqfish.spdnet.net.windows.WndServerInfo;
import com.saqfish.spdnet.sprites.ItemSprite;
import com.saqfish.spdnet.sprites.ItemSpriteSheet;
import com.saqfish.spdnet.ui.Archs;
import com.saqfish.spdnet.ui.Icons;
import com.saqfish.spdnet.net.events.Send;
import com.saqfish.spdnet.net.windows.NetWindow;
import com.saqfish.spdnet.ui.RedButton;
import com.saqfish.spdnet.ui.RenderedTextBlock;
import com.saqfish.spdnet.ui.StyledButton;
import com.saqfish.spdnet.windows.WndGameInProgress;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Callback;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Random;

import static com.saqfish.spdnet.Dungeon.seed;
import static com.saqfish.spdnet.ShatteredPixelDungeon.net;

public class AmuletScene extends PixelScene {
	
	private static final int WIDTH			= 120;
	private static final int BTN_HEIGHT		= 20;
	private static final float SMALL_GAP	= 2;
	private static final float LARGE_GAP	= 8;
	
	public static boolean noText = false;
	WndServerInfo self = new WndServerInfo();
	private Image amulet;

	{
		inGameScene = true;
	}

	StyledButton btnExit = null;
	StyledButton btnStay = null;

	@Override
	public void create() {
		super.create();
		
		RenderedTextBlock text = null;
		if (!noText) {
			text = renderTextBlock( Messages.get(this, "text"), 8 );
			text.maxWidth( PixelScene.landscape() ? 2*WIDTH-4 : WIDTH);
			add( text );
		}
		
		amulet = new Image( Assets.Sprites.AMULET );
		add( amulet );

		btnExit = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "exit") ) {
			@Override
			protected void onClick() {

				if(net().connected()) {
					Sender.sendWin();
					Dungeon.win(Amulet.class);
					Dungeon.deleteGame(GamesInProgress.curSlot, true);
					btnExit.enable(false);
					btnStay.enable(false);
					AmuletScene.this.add(new Delayer(0.1f){
						@Override
						protected void onComplete() {
							if (BadgeBanner.isShowingBadges()){
								AmuletScene.this.add(new Delayer(3f){
									@Override
									protected void onComplete() {
										Game.switchScene( RankingsScene.class );
									}
								});
							} else {
								Game.switchScene( RankingsScene.class );
							}
						}
					});
				} else {
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							NetWindow.runWindow(new WndNetOptions(Icons.get(Icons.INFO),
									Messages.get(NetWindow.class,"info"),
									Messages.get(StartScene.class,"localseed")+seed+"\n"+
											Messages.get(AmuletScene.class,"really"),
									Messages.get(StartScene.class,"reload")){
								@Override
								protected void onSelect(int index) {
									super.onSelect(index);
									if (index ==0){
										net().toggle(self);
									}
								}
							});
						}
					});
				}
			}
		};
		btnExit.icon(new ItemSprite(ItemSpriteSheet.AMULET));
		btnExit.setSize( WIDTH, BTN_HEIGHT );
		add( btnExit );
		
		btnStay = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "stay") ) {
			@Override
			protected void onClick() {
				onBackPressed();
				btnExit.enable(false);
				btnStay.enable(false);
			}
		};
		btnStay.icon(Icons.CLOSE.get());
		btnStay.setSize( WIDTH, BTN_HEIGHT );
		add( btnStay );
		
		float height;
		if (noText) {
			height = amulet.height + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height();
			
			amulet.x = (Camera.main.width - amulet.width) / 2;
			amulet.y = (Camera.main.height - height) / 2;
			align(amulet);

			btnExit.setPos( (Camera.main.width - btnExit.width()) / 2, amulet.y + amulet.height + LARGE_GAP );
			btnStay.setPos( btnExit.left(), btnExit.bottom() + SMALL_GAP );
			
		} else {
			height = amulet.height + LARGE_GAP + text.height() + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height();
			
			amulet.x = (Camera.main.width - amulet.width) / 2;
			amulet.y = (Camera.main.height - height) / 2;
			align(amulet);

			text.setPos((Camera.main.width - text.width()) / 2, amulet.y + amulet.height + LARGE_GAP);
			align(text);
			
			btnExit.setPos( (Camera.main.width - btnExit.width()) / 2, text.top() + text.height() + LARGE_GAP );
			btnStay.setPos( btnExit.left(), btnExit.bottom() + SMALL_GAP );
		}

		new Flare( 8, 48 ).color( 0xFFDDBB, true ).show( amulet, 0 ).angularSpeed = +30;
		
		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		if (btnExit.isActive()) {
			InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
			Game.switchScene(InterlevelScene.class);
		}
	}
	
	private float timer = 0;
	
	@Override
	public void update() {
		super.update();
		
		if ((timer -= Game.elapsed) < 0) {
			timer = Random.Float( 0.5f, 5f );
			
			Speck star = (Speck)recycle( Speck.class );
			star.reset( 0, amulet.x + 10.5f, amulet.y + 5.5f, Speck.DISCOVER );
			add( star );
		}
	}
}
