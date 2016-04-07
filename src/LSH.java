import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * Implementation of minhash and locality sensitive hashing (lsh) to find similar objects.
 * 
 * The LSH should first construct a signature matrix. Based on this, LSH is performed resulting in a mapping of band ids to hash tables (stored in bandToBuckets).
 * From this bandsToBuckets mapping, the most similar items should then be retrieved.
 * 
 * @author Toon Van Craenendonck
 *
 */
public class LSH extends SimilaritySearcher{
	List<Map<String, Set<Integer>>> bandToBuckets;

	/**
	 * Construct an LSH similarity searcher.
	 * 
	 * @param objectMapping objects and their set representations of which similarity should be searched
	 * @param numHashes number of hashes to use to construct the signature matrix
	 * @param numBands number of bands to use during locality sensitive hashing
	 * @param numValues the number of unique values that occur in the objects' set representations (i.e. the number of rows of the original characteristic matrix)
	 * @param rand should be used to generate any random numbers needed
	 */
	public LSH(Map<Integer, Set<Integer>> objectMapping, int numHashes, int numBands, int numValues, Random rand){
		super(objectMapping);
		
		int prime  = Primes.findLeastPrimeNumber(numValues);
		int[][] hashValues = LSH.constructHashTable(numHashes, numValues, prime, rand);
		int[][] signatureMatrix = LSH.constructSignatureMatrix(objectMapping, hashValues);
		bandToBuckets = LSH.lsh(signatureMatrix, numBands);
	}

	/**
	 * Returns the band to buckets mapping.
	 * @return
	 */
	public List<Map<String, Set<Integer>>> getBandToBuckets(){
		return bandToBuckets;
	}

	/**
	 * Construct the table of hash values needed to construct the signature matrix.
	 * Position (i,j) contains the result of applying function j to row number i.
	 * @param numHashes number of hashes that will be used in the signature matrix
	 * @param numValues number of unique values that occur in the object set representations (i.e. number of rows of the characteristic matrix)
	 * @param prime prime needed for universal hashing
	 * @param rand object to generate random numbers
	 * @return the (numValues x numHashes) matrix of hash values
	 */
	public static int[][] constructHashTable(int numHashes, int numValues, int prime, Random rand) {
		int[][] hashes = new int[numValues][numHashes];

		/* Fill in here */
		
		return hashes;
	}

	/**
	 * Constructing the signature matrix.
	 * 
	 * @param objectMapping objects and their set representations for which the signature matrix should be constructed
	 * @param hashValues (numValues x numHashes) matrix of hash values
	 * @return the (numHashes x numObjects) signature matrix
	 */
	public static int[][] constructSignatureMatrix(Map<Integer, Set<Integer>> objectMapping, int[][] hashValues) {
		int numHashes = hashValues[0].length;
		int numObjects = objectMapping.size();

		// initialize it to max int values
		int[][] signatureMatrix = new int[numHashes][numObjects];

		/* Fill in here */ 

		return signatureMatrix;

	}

	/**
	 * Perform locality sensitive hashing.
	 * 
	 * @param signatureMatrix previously constructed signature matrix
	 * @param numBands the number of bands to use
	 * @return mapping of bands to corresponding hash maps, these hash maps in turn map keys (being parts of the signature matrix) to sets of ids of potentially similar objects
	 */
	public static List<Map<String, Set<Integer>>> lsh(int[][] signatureMatrix, int numBands) {
		List<Map<String, Set<Integer>>> bandToBuckets =
		    new ArrayList<Map<String, Set<Integer>>>();

		/* Fill in here */ 

		return bandToBuckets;

	}

	/**
	 * Returns the pairs with similarity above threshold (approximate).
	 */
	@Override
	public Set<SimilarPair> getSimilarPairsAboveThreshold(double threshold) {
		List<SimilarPair> cands = new ArrayList<SimilarPair>();
		
		/* Fill in here */ 

		return new HashSet<SimilarPair>(cands);

	}

	/**
	 * Get the objects that have a similarity above threshold thr to the object identified by the given object id objID
	 * @param objID the object of which we want to search neighbors
	 * @param thr the similarity threshold
	 * @return the objects with similarity above thr
	 */
	@Override
	public Set<Neighbor> getNeighborsAboveThreshold(int internalID, double thr) {
		Set<Neighbor> candidateNeighbors = new HashSet<Neighbor>();
		
		/* Fill in here (only required for movie rating predictions */ 

		return candidateNeighbors;
	}


}
