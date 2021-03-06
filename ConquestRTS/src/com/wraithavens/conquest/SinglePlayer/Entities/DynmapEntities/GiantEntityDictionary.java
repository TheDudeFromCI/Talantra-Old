package com.wraithavens.conquest.SinglePlayer.Entities.DynmapEntities;

import java.util.HashMap;
import java.util.Random;
import com.wraithavens.conquest.SinglePlayer.Entities.EntityType;
import com.wraithavens.conquest.SinglePlayer.Noise.Biome;

public class GiantEntityDictionary{
	private static final long s = 4294967291L;
	private final HashMap<Biome,DictionaryEntry> biomes = new HashMap();
	private final float averageDistanceApart;
	private final Random r = new Random();
	public GiantEntityDictionary(){
		HashMap<Biome,TempDictionaryEntry> averageDistances = new HashMap();
		averageDistances.put(Biome.ArcstoneHills, new TempDictionaryEntry(100f, EntityType.Arcstone, 30));
		averageDistanceApart = rebuild(averageDistances)*2;
	}
	public float getAverageDistance(){
		return averageDistanceApart;
	}
	public float getMinDistance(){
		return averageDistanceApart/2;
	}
	public EntityType randomEntity(Biome biome, long seed, int x, int y){
		long t = seed;
		t = t*s+x;
		t = t*s+y;
		t += x*x+s;
		t += y*y+s;
		r.setSeed(t);
		DictionaryEntry e = biomes.get(biome);
		if(e==null)
			return null;
		if(r.nextFloat()>e.getSpawnChance())
			return null;
		return EntityType.getVariation(e.getEntity(), (int)(r.nextFloat()*e.getVariations()));
	}
	private float rebuild(HashMap<Biome,TempDictionaryEntry> averageDistances){
		float minDistance = Float.MAX_VALUE;
		float d;
		for(Biome biome : averageDistances.keySet()){
			d = averageDistances.get(biome).getAverageDistance();
			if(d<minDistance)
				minDistance = d;
		}
		TempDictionaryEntry entry;
		for(Biome biome : averageDistances.keySet()){
			entry = averageDistances.get(biome);
			biomes.put(biome, new DictionaryEntry(entry.getAverageDistance()/minDistance, entry.getMainEntity(),
				entry.getEntityVariations()));
		}
		return minDistance;
	}
}
