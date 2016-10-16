package org.iis.ut.artificialplagiarismcreator.plagiarism.dataset.artificial;

import ir.sbu.nlp.wordnet.data.model.FNSense;
import ir.sbu.nlp.wordnet.data.model.FNSensesRelation;
import ir.sbu.nlp.wordnet.data.model.FNSynset;
import ir.sbu.nlp.wordnet.data.model.FNSynsetsRelation;
import ir.sbu.nlp.wordnet.service.FNSynsetService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import edu.mit.jwi.item.Pointer;

public class FarsnetUtility {

	private  final FNSynsetService service=new FNSynsetService();
	/**
	 * @param args
	 */
	
	public  String convertToFarsnetPOS(String pos)
	{
		pos = pos.toLowerCase();
		if(pos.startsWith("n_"))
		{
			return "Noun";
		}
		
		if(pos.startsWith("v_"))
		{
			return "Verb";
		}
		
		if(pos.equals("p"))
		{
			return "Pronoun";
		}
		
		if(pos.startsWith("adj_"))
		{
			return "Adjective";
		}
		
		if(pos.startsWith("adv_"))
		{
			return "Adverb";
		}
		
		if(pos.equals("noun"))
		{
			return "Noun";
		}
		
		if(pos.equals("adv"))
		{
			return "Adverb";
		}
		
		if(pos.equals("adj"))
		{
			return "Adjective";
		}
		return pos;
	}
	
	public  void main(String[] args) {
		//	findSynsetsByWord("مزاحم");
		List<String> context = new ArrayList<String>();

		String word = "مزاحم";
		String pos = "Noun";// Adjective // Adverb

		String alter = getAlternateWord(context, word, pos);

		String word2 = "بد";
		String pos2 = "Adjective";



		String alter2 = getAlternateWord(context, word2, pos2);


		String word3 = "خیلی";
		String pos3 = "Adverb";



		String alter3 = getAlternateWord(context, word3, pos3);


		String word4 = "خرید";
		String pos4 = "verb";



		String alter4 = getAlternateWord(context, word4, pos4);
	}


	public  String getAlternateWord(List<String> context, String word,
			String pos) {
		Long synsetId = getReplacementOptions(word,pos, context);
		//synonym or hypernym or hyponym
		FNSynset synset = service.findSynsetById(synsetId);

		String replacedWord = replaceWord(synset);
		return replacedWord;
	}


	public static String replaceWord(FNSynset synset) {
		ToBeReplacedWord toBeReplacedWord = new ToBeReplacedWord();
		toBeReplacedWord.addCandidWordRelationType("synonym");
		toBeReplacedWord.addCandidWordRelationType("hypernym");
		toBeReplacedWord.addCandidWordRelationType("hyponym");
		toBeReplacedWord.addCandidWordRelationType("holonym_member");
		toBeReplacedWord.addCandidWordRelationType("holonym_part");
		toBeReplacedWord.addCandidWordRelationType("holonym_substance");
		toBeReplacedWord.addCandidWordRelationType("hypernym_instance");
		toBeReplacedWord.addCandidWordRelationType("hyponym_instance");


		toBeReplacedWord.addCandidWordRelationType("meronym_member");
		toBeReplacedWord.addCandidWordRelationType("meronym_part");
		toBeReplacedWord.addCandidWordRelationType("meronym_substance");

		if(synset != null)
		{
			for(FNSense synonym : synset.getSenses())
			{
				toBeReplacedWord.addCandidate(synonym.getWord().ShowValue(), "synonym");
				Vector<FNSensesRelation> relations = synonym.getSensesRelations();

				for(FNSensesRelation relation: relations)
				{
					if(relation.getRelationType().toLowerCase().equals(Pointer.HYPERNYM.toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "hypernym");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.HYPONYM.toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "hyponym");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.HOLONYM_MEMBER .toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "holonym_member");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.HOLONYM_PART.toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "holonym_part");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.HOLONYM_SUBSTANCE.toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "holonym_substance");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.HYPERNYM_INSTANCE .toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "hypernym_instance");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.MERONYM_MEMBER .toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "meronym_member");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.MERONYM_PART.toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "meronym_part");
					}
					else if(relation.getRelationType().toLowerCase().equals(Pointer.MERONYM_SUBSTANCE.toString().toLowerCase()))
					{
						toBeReplacedWord.addCandidate(relation.getSecondSense().getWord().ShowValue(), "meronym_substance");
					}
				}
			}
		}

		return toBeReplacedWord.getRandomCandidate();
	}


	public static void computeFarsnetCoverage()
	{
	}


	public void findSynsetsByWord(String word,List<String> sentence)
	{


		//find all synset containd the word
		Vector<FNSynset> fnSynsets=service.FindSynsetsByWord(word); //print every synset

		for(int i=0;i<fnSynsets.size();i++)
		{ 		
			FNSynset fnSynset=fnSynsets.elementAt(i); 
			System.out.println("Gloss: "+fnSynset.getGloss());
			System.out.println("Semantic Category: "+fnSynset.getSemanticCategory());

			Vector<FNSynsetsRelation>  synsetRelations = fnSynset.getSynsetsRelations();
			for(FNSynsetsRelation relation: synsetRelations)
			{

				System.out.println("***"+relation.getRelType()+" "+relation.getSynset_2().getGloss()+" "+relation.getSynset_2().getSenses().get(0));
			}
			System.out.println("Different Senses of the Synset:");
			for(int j=0;j<fnSynset.getSenses().size();j++){
				FNSense fnSense=fnSynset.getSenses().elementAt(j);
				System.out.println(fnSense.getWord().getValue().elementAt(0)); 	

				Vector<FNSensesRelation> senseRelations = fnSense.getSensesRelations();
				for(FNSensesRelation senseRelation: senseRelations)
				{
					System.out.println("#####"+senseRelation.getRelationType()+":"+senseRelation.getSecondSense().getWord().ShowValue()+" "+senseRelation.getFirstSense().getWord().ShowValue());					
				}
			}

		}
	}
	
	

	public  Long getReplacementOptions(String wordstem, String pos, List<String> context)
	{
		//find all synset containd the word
		Vector<FNSynset> fnSynsets= service.FindSynsetsByWord(wordstem);
		Vector<FNSynset> pos_related_synset = new Vector<FNSynset>();
		Map<Long,Map<String, Double>> senseBags = new HashMap<Long,Map<String, Double>>();
		for(FNSynset synset: fnSynsets)
		{
			System.out.println(synset.getPos());
			if(synset.getPos().equals(pos))
			{
				pos_related_synset.add(synset);
				if(!senseBags.containsKey(synset.getId()))
					senseBags.put(synset.getId(), new HashMap<String,Double>());

				for(FNSense sense: synset.getSenses())
				{
					senseBags.get(synset.getId()).put(sense.getWord().ShowValue(), 1.0);					
					Vector<FNSensesRelation> senseRelations = sense.getSensesRelations();
					for(FNSensesRelation senseRelation: senseRelations)
					{
						if(senseRelation.getRelationType().toLowerCase().equals(Pointer.HYPERNYM.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.ATTRIBUTE.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.HYPERNYM_INSTANCE.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.HYPONYM.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.HOLONYM_MEMBER.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.HYPONYM_INSTANCE.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.HOLONYM_PART.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.HOLONYM_SUBSTANCE.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.DERIVATIONALLY_RELATED.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
						else if(senseRelation.getRelationType().toLowerCase().equals(Pointer.ALSO_SEE.toString().toLowerCase()))
						{
							if(!senseBags.containsKey(senseRelation.getSecondSense().getSynset().getId()))
								senseBags.put(senseRelation.getSecondSense().getSynset().getId(), new HashMap<String,Double>());

							senseBags.get(senseRelation.getSecondSense().getSynset().getId()).put(senseRelation.getSecondSense().getWord().ShowValue(), 0.5);
						}
					}
				}

				Vector<FNSynsetsRelation> synsetsRelation = synset.getSynsetsRelations();
				for(FNSynsetsRelation synsetRelation: synsetsRelation)
				{
					if(synsetRelation.getRelType().toLowerCase().equals(Pointer.HYPERNYM.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.ATTRIBUTE.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.HYPERNYM_INSTANCE.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.HYPONYM.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.HOLONYM_MEMBER.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.HYPONYM_INSTANCE.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.HOLONYM_PART.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.HOLONYM_SUBSTANCE.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.DERIVATIONALLY_RELATED.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
					else if(synsetRelation.getRelType().toLowerCase().equals(Pointer.ALSO_SEE.toString().toLowerCase()))
					{
						if(!senseBags.containsKey(synsetRelation.getSynset_2().getId()))
							senseBags.put(synsetRelation.getSynset_2().getId(), new HashMap<String,Double>());
						for(FNSense sense: synsetRelation.getSynset_2().getSenses())
						{
							senseBags.get(synsetRelation.getSynset_2().getId()).put(sense.getWord().ShowValue(), 0.5);
						}
					}
				}

			}
		}


		System.out.println("Number of sense bags:"+senseBags.size());
		Integer MaxScore = 0;
		Long MaxScoreSynsetId = -1L;
		for(Long key1: senseBags.keySet())
		{
			Integer score = 0;

			for(String sense: senseBags.get(key1).keySet())
			{
				System.out.print(sense+ " ");
				if(context.contains(sense))
				{
					score++;
				}
			}

			if(score >= MaxScore)
			{
				MaxScore = score;
				MaxScoreSynsetId = key1;
			}
			System.out.println();
		}

		return MaxScoreSynsetId;
	}
}

class ToBeReplacedWord{
	private Map<String, List<String>> candids = new HashMap<String, List<String>>();
	private static final Random randomGenerator = new Random();

	public void addCandidWordRelationType(String relationType)
	{
		if(!candids.containsKey(relationType))
		{
			candids.put(relationType, new ArrayList<String>());
		}
	}

	public void addCandidate(String candid, String relationType)
	{
		if(candids.containsKey(relationType))
			candids.get(relationType).add(candid);
	}


	public String getRandomCandidate()
	{
		List<String> allWords = new ArrayList<String>();

		for(List<String> list:candids.values())
		{
			allWords.addAll(list);
		}

		if(allWords.size() > 0)
		{
			int i = randomGenerator.nextInt(allWords.size());
			return allWords.get(i);
		}

		return "";
	}


}