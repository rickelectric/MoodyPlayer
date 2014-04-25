package moodplayer.api;

import java.util.ArrayList;

import moodplayer.models.Genre;
import moodplayer.util.UtilBox;

public class EchoGenres {
	
	public static String[]
		ALTERNATIVE={
			"alternative country","alternative dance","alternative emo",
			"alternative hip hop","alternative metal","alternative metalcore",
			"alternative new age","alternative pop","alternative pop rock",
			"alternative r&b","alternative rock","british alternative rock",
			"gothic alternative","hard alternative","heavy alternative","adult album alternative"
			
		},
		BLUES={
			"acoustic blues","blues","blues-rock","country blues","electric blues",
			"jazz-blues","modern-blues","power blues-rock",
			"punk blues","soul blues",
		},
		CLASSICAL={
		"classical","classical christmas","classical guitar","classic rock",
		"classical organ","classical period","classical piano","contemporary classical",
		"modern classical","neoclassical",
		},
		COUNTRY={
			"contemporary country","counry","country blues",
			"country christmas","country gospel","country rock",
			"modern country rock","alternative country"
		},
		DANCEHALL={
			"dancehall"
		},
		ELECTRONIC={
			"electronic","electronica","power electronics","electric blues",
			"electro","electro dub","electro house","electro jazz",
			"electro latino","electro swing","electro trash","electro-industrial",
			"electroacoustic improvisation","electroclash"
		},
		FOLK={
			"contemporary folk","ectofolk","folk","folk christmas",
			"folk metal","folk punk","folk rock","folk-pop",
			"folklore argentino","folkmusik","freak folk","geek folk",
			"indie folk","neofolk"
		},
		FUNK_SOUL={
			"classic funk rock","deep funk","funk","funk metal",
			"funk rock","funky breaks","jazz funk","neurofunk",
			"neo soul","neo soul-jazz","soul","soul blues",
			"soul christmas","soul flow","soul jazz",
		},
		HIP_HOP={
			"christian hip hop","detroit hip hop","hardcore hip hop",
			"hip hop","old school hip hop","outer hip hop","abstract hip hop",
			"alternative hip hop"
		},
		INDIE={
			"british indie rock","indie christmas","indie folk",
			"indie pop","indie psych-pop","indie rock","indie shoegaze",
			"indie singer-songwriter","more indie pop","more indie rock",
			"more indie singer-songwriter"
		},
		JAZZ={
			"contemporary jazz","cool jazz","electro jazz",
			"free jazz","gypsy jazz","jazz","jazz bass",
			"jazz blues","jazz christmas","jazz funk","jazz fusion",
			"jazz metal","jazz orchestra","jazz trio",
			"neo soul-jazz","smooth jazz","soul jazz","acid jazz"
		},
		METAL={
			"alternative metal","alternative metalcore","atmospheric post-metal",
			"black metal","chaotic black metal","death metal",
			"doom metal","folk metal","funk metal","glam metal",
			"gothic metal","gothic symphonic metal","groove metal",
			"industrial metal","jazz metal","melodic death metal",
			"melodic metalcore","melodic power metal","melodic progressive metal",
			"metal","metal guitar","metalcore","neo classical metal","pagan black metal",
			"post-metal","power metal","progressive metal","rap metal","rap metalcore",
			"raw black metal","retro metal","slam death metal","sludge metal",
			"speed metal","stoner metal","symphonic black metal"
		},
		POP={
			"acoustic pop","alternative pop","alternative pop rock","dance pop","dream pop",
			"pop","pop rock","folk pop","indie pop","power pop","garage pop","hip pop",
			"noise pop","pop emo","pop house","pop punk","pop rap","power pop",
			"power pop punk"
		},
		REGGAE={
			"reggae","reggae fusion","reggae rock","reggaeton",
			"roots reggae","skinhead reggae"
		},
		RNB={
			"r&b"
		},
		ROCK={
			"alternative rock","rock","pop rock","country rock","dance rock","folk rock",
			"funk rock","garage rock","gothic rock","hard rock",
			"hard stoner rock","indie rock","industrial rock","lovers rock",
			"roots rock",
		};
		
	public static String[] getGenreTerms(Genre g){
		if(g==null) return null;
		switch(g){
			case ALTERNATIVE: 	return ALTERNATIVE;
			case BLUES: 		return BLUES;
			case CLASSICAL: 	return CLASSICAL;
			case COUNTRY: 		return COUNTRY;
			case DANCEHALL: 	return DANCEHALL;
			case ELECTRONIC:	return ELECTRONIC;
			case FOLK:			return FOLK;
			case FUNK_SOUL: 	return FUNK_SOUL;
			case HIP_HOP:		return HIP_HOP;
			case INDIE:			return INDIE;
			case JAZZ:			return JAZZ;
			case METAL:			return METAL;
			case POP:			return POP;
			case REGGAE:		return ROCK;
			case RNB:			return RNB;
			case ROCK:			return ROCK;
			default:			return null;
		}
	}
	
	/**
	 * 
	 * @param d ReKognition Gender Value 0(Female) ... 1(Male)
	 * @return
	 */
	public static String[] genderSpecific(double d){
		if(d<0.4){
			//Female
			return new String[]{
				"pop","dance pop",
				"contemporary classical",
				"contemporary country",
				"contemporary folk",
				"contemporary jazz",
				"contemporary post-bop",
				"hot adult contemporary",
				"urban contemporary",
				"r&b","latin pop","teen pop",
				"neo soul","latin","pop rock",
			};
		}
		else if(d>0.6){
			//Male
			return new String[]{
				"rock","hip hop","house","album rock",
				"rap","pop rap","indie rock",
				"funk rock","gangster rap",
				"electro house","classic rock",
				"nu metal"
			};
		}
		else{
			//Unsure / Unknown
			return null;
		}
	}
	
	public static String[] getGenresTerms(Genre main,String term){
		String[] g=getGenreTerms(main);
		ArrayList<String> str=new ArrayList<String>();
		for(String s:g){
			if(s.contains(term)) str.add(s);
		}
		if(str.isEmpty()) return null;
		return str.toArray(new String[str.size()]);
	}

	public static String getRandom() {
		Genre[] vals=Genre.values();
		int n=UtilBox.getRandomNumber(vals.length);
		Genre sel=vals[n];
		String[] terms=getGenreTerms(sel);
		n=UtilBox.getRandomNumber(terms.length);
		return terms[n];
	}
		
		
		
	
	
}
