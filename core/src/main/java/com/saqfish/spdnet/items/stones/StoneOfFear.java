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

package com.saqfish.spdnet.items.stones;

import com.saqfish.spdnet.Assets;
import com.saqfish.spdnet.Dungeon;
import com.saqfish.spdnet.actors.Actor;
import com.saqfish.spdnet.actors.Char;
import com.saqfish.spdnet.actors.buffs.Buff;
import com.saqfish.spdnet.actors.buffs.Terror;
import com.saqfish.spdnet.effects.Flare;
import com.saqfish.spdnet.sprites.ItemSpriteSheet;
import com.saqfish.spdnet.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;

public class StoneOfFear extends Runestone {
	
	{
		image = ItemSpriteSheet.STONE_FEAR;
	}
	
	@Override
	protected void activate(int cell) {

		Char ch = Actor.findChar( cell );

		if (ch != null){
			Buff.affect( ch, Terror.class, Terror.DURATION ).object = curUser.id();
		}

		new Flare( 5, 16 ).color( 0xFF0000, true ).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2f );
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
	}
	
}