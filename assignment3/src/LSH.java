import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Arrays;
import java.util.Iterator;


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
    int[] a = new int[numHashes];
    int[] b = new int[numHashes];

    for (int j = 0; j < numHashes; j++) {
      do {
        a[j] = rand.nextInt(prime);
      } while (a[j] == 0);
      b[j] = rand.nextInt(prime);
    }

    for (int i = 0; i < numValues; i++) {
      for (int j = 0; j < numHashes; j++) {
        hashes[i][j] = ((a[j] * i + b[j]) % prime) % numValues;
      }
    }
		
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
		int numObjects = objectMapping.size(); // NOT equal to numValues in constructHashTable

		// initialize it to max int values
		int[][] signatureMatrix = new int[numHashes][numObjects];
    int[] max_values = new int[numObjects];
    Arrays.fill(max_values, Integer.MAX_VALUE);
    // fill would use the same reference for each row
    for (int i = 0; i < numHashes; i++) {
      signatureMatrix[i] = max_values.clone();
    }

    for (int obj = 0; obj < numObjects; obj++) {
      for (int r: objectMapping.get(obj)) {
        for (int i = 0; i < numHashes; i++) {
          int sig_i = signatureMatrix[i][obj];
          int h_iofr = hashValues[r][i];
          if (h_iofr < sig_i) {
            signatureMatrix[i][obj] = h_iofr;
          }
        }
      }
    }

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

    int rowsPerBand = signatureMatrix.length / numBands;

    int bandStart = 0;
    for (int band = 0; band < numBands; band++) {
      bandToBuckets.add(band, new HashMap<String, Set<Integer>>());
      bandStart = band * rowsPerBand;
      // easy parallel traversal?
      for (int obj = 0; obj < signatureMatrix[0].length; obj++) {
        // 10 = average length of hash in digits
        StringBuilder sb = new StringBuilder(10 * rowsPerBand);
        for (int r = 0; r < rowsPerBand; r++) {
          sb.append(signatureMatrix[bandStart+r][obj]);
        }
        String key = sb.toString();
        if (bandToBuckets.get(band).get(key) == null) {
          bandToBuckets.get(band).put(key, new HashSet<Integer>());
        }
        bandToBuckets.get(band).get(key).add(obj); 
      }
    }

		return bandToBuckets;

	}
  private double jaccard(int first, int second) {
    Set<Integer> intersection = new HashSet<Integer>(objectMapping.get(first));
    intersection.retainAll(objectMapping.get(second));
    Set<Integer> union = new HashSet<Integer>(objectMapping.get(first));
    union.addAll(objectMapping.get(second));

    return ((double) intersection.size()) / union.size();
  }

	/**
	 * Returns the pairs with similarity above threshold (approximate).
	 */
	@Override
	public Set<SimilarPair> getSimilarPairsAboveThreshold(double threshold) {
		List<SimilarPair> cands = new ArrayList<SimilarPair>();
		
    for (Map<String, Set<Integer>> band : bandToBuckets) {
      for (Set<Integer> bucket : band.values()) {
        Iterator<Integer> it = bucket.iterator();
        while (it.hasNext()) {
          int first = it.next();
          it.remove(); // Don't compare to itself (or previously viewed)
          for (int second : bucket) {
            double similarity = jaccard(first, second);
            if (similarity > threshold) {
              cands.add(new SimilarPair(first, second, similarity));
            }
          }
        }
      }
    }

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
