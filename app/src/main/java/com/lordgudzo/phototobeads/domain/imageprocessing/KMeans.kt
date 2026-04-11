package com.lordgudzo.phototobeads.domain.imageprocessing

import com.lordgudzo.phototobeads.domain.model.LabPoint
import kotlin.random.Random

/**
 * Gpt chat and deepseek helped me with this algorithm.
 *
 * For example:
 *
 * labPixels: Array<LabPoint>
 * LabPoint(10, 0, 0)   // dark
 * LabPoint(12, 1, 1)   // dark similar
 * LabPoint(80, 0, 0)   // bright
 * LabPoint(82, 2, 1)   // bright similar
 *
 * colorCount = 2
 *
 * Return LabPoint(l=11.0, a=0.5, b=0.5) and LabPoint(l=81.0, a=1.0, b=0.5)
 * */
class KMeans {
    /**
     * @param labPixels Array<LabPoint> with pixels from image in LAB
     * @param colorCount how many colors will be in the beading pattern
     * If will be big image, will be take 5000 random pixels.
     * @return ArrayList of size colorCount, its consist LAB colors for the beading pattern.
     * For our example it will be LabPoint(l=11.0, a=0.5, b=0.5) and LabPoint(l=81.0, a=1.0, b=0.5)
     * */
    fun apply(labPixels: Array<LabPoint>, colorCount: Int): List<LabPoint> {
        //If will be big image, will be take 5000 random pixels.
        val sampledPixels: List<LabPoint>
        if (labPixels.size > 5000) {
            //takes 5000 random unique indices
            val sampledIndices = labPixels.indices.shuffled().take(5000)
            //takes pixels from Array<LabPoint> with random indices from sampledIndices
            sampledPixels = sampledIndices.map { labPixels[it] }
        } else sampledPixels = labPixels.toList()

        return kMeansQuantize(sampledPixels, colorCount)
    }


    /**
     * K-means++ clustering. With this algorithm
     * This algorithm consist of 2 steps.
     * Step 1. initCentersKMeans - smart selection of cluster centers for best works kMeansIterations.
     * Step 2. kMeansIterations - calculate means Lab color in the clusters.     *
     *
     * @param sampledPixels Array<LabPoint> with pixels from image in LAB.
     *      For big image List with 5000 pixels, else all pixels.
     * @param clusters how many LAB colors for the beading pattern needs.
     * @return ArrayList of size colorCount, its consist LAB colors for the beading pattern.
     */
    private fun kMeansQuantize(sampledPixels: List<LabPoint>, clusters: Int): List<LabPoint> {
        if (sampledPixels.isEmpty() || clusters == 0) return emptyList()
        val random = Random(System.currentTimeMillis())

        //for example LabPoint(l=82.0, a=2.0, b=1.0) and LabPoint(l=12.0, a=1.0, b=1.0)
        val centers: MutableList<LabPoint> = initCentersKMeans(sampledPixels, clusters, random)
        return kMeansIterations(sampledPixels,random, centers)
    }



    /**
     * Block 1  K-means initCenters
     * Smart selection of cluster centers for best works kMeansIterations.
     * @param sampledPixels Array<LabPoint> with pixels from image in LAB.
     *      For big image List with 5000 pixels, else all pixels.
     * @param clusters: how many LAB colors for the beading pattern needs.
     * @return MutableList with centers of clusters.
     */
    private fun initCentersKMeans(
        sampledPixels: List<LabPoint>,
        clusters: Int,
        random: Random
    ): MutableList<LabPoint> {
        // Step 1: Pick first center randomly.
        // Example: random picks index 1 -> LabPoint(12, 1, 1)
        val centers = mutableListOf(sampledPixels[random.nextInt(sampledPixels.size)])

        // Step 2: Keep adding new centers until we have 'clusters' centers.
        while (centers.size < clusters) {
            /**
             * For each point, find the distance to the nearest existing center.
             * We have only one center now: (12, 1, 1)
             *
             * squaredDistance from center (12,1,1) to:
             *   point0 (10,0,0) -> (10-12)^2 + (0-1)^2 + (0-1)^2 = 4+1+1 = 6
             *   point1 (12,1,1) -> 0
             *   point2 (80,0,0) -> (80-12)^2 + (0-1)^2 + (0-1)^2 = 4624+1+1 = 4626
             *   point3 (82,2,1) -> (82-12)^2 + (2-1)^2 + (1-1)^2 = 4900+1+0 = 4901
             *
             * So distances list = [6.0, 0.0, 4626.0, 4901.0]
             */
            val distances: List<Double> =
                sampledPixels.map { point -> centers.minOf { it.checkLabPointDistance(point) } }
            // Sum all distances: 6 + 0 + 4626 + 4901 = 9533.0
            val total = distances.sum()
            // All points are exactly at centers -> stop
            if (total == 0.0) break


            /**
             * Imagine a long line between 0 .. 9533
             * This block chooses a point at random, but also that the far points have a better chance
             * so 4901 and 4626 has more chances to become as new clusters
             * */
            //random point between 0 .. 9533
            var randomPoint = random.nextDouble() * total
            var currentIndex = 0
            // Walk along the line, subtract segment lengths until randomPoint becomes <= 0.
            while (randomPoint > 0 && currentIndex < distances.size) {
                randomPoint -= distances[currentIndex]; // Subtract the segment of current point
                currentIndex++                          // Move to next point
            }
            // When randomValue becomes <= 0, we found our point
            // The chosen point is at index (currentIndex - 1).
            // coerceIn ensures the index is valid (never less than 0 or more than last index).
            centers.add(sampledPixels[(currentIndex - 1).coerceIn(sampledPixels.indices)])
        }
        return centers
    }



    /** Block 2  K-means iterations
     *  For example centers = LabPoint(l=82.0, a=2.0, b=1.0) and LabPoint(l=12.0, a=1.0, b=1.0)
     *
     *  Step 1: assign points to clusters
     *  For each point we store index of assigned cluster
     *  If we have 2 clusters, points will be assigned to 0 and 1 groups
     *  For Example for our centers:
     *  LabPoint(10, 0, 0) near centers (l=12.0, a=1.0, b=1.0) it's 1
     *  LabPoint(12, 1, 1) near centers (l=12.0, a=1.0, b=1.0) it's 1
     *  LabPoint(80, 0, 0) near centers (l=82.0, a=2.0, b=1.0) it's 0
     *  LabPoint(82, 2, 1) near centers (l=82.0, a=2.0, b=1.0) it's 0
     *  In the end step one we have pointToCluster array[1,1,0,0]
     *
     *  Step 2: calculate sums for each cluster.
     *  We sum all points inside each cluster
     *  Example:
     *  pointToCluster = [1,1,0,0]
     *  Cluster 0 points: (80,0,0), (82,2,1) → sum = (162, 2, 1)  clustersSums[0] = (162, 2, 1)
     *  Cluster 1 points: (10,0,0), (12,1,1) → sum = (22, 1, 1)   clustersSums[1] = (22, 1, 1)
     *  clustersSizes[0] = 2
     *  clustersSizes[1] = 2
     *
     *  Step 3: Calculate means value in clusters.
     *  For our example will return  LabPoint(l=11.0, a=0.5, b=0.5) and LabPoint(l=81.0, a=1.0, b=0.5)
     *
     * @param sampledPixels Array<LabPoint> with pixels from image in LAB.
     *      For big image List with 5000 pixels, else all pixels.
     *  @param centers MutableList with centers of clusters after K-means initCenters
     *  @return ArrayList of size colorCount, its consist LAB colors for the beading pattern.
     * */
    private fun kMeansIterations(
        sampledPixels: List<LabPoint>,
        random: Random,
        centers: MutableList<LabPoint>
    ): MutableList<LabPoint> {
        val pointToCluster = IntArray(sampledPixels.size)

        var iteration = 0
        val maxIterations = 20

        while (iteration < maxIterations) {

            //<editor-fold desc="Step 1: assign points to clusters">
            val changed = assignPointsToClusters(
                sampledPixels,
                centers,
                pointToCluster
            )
            if (!changed) break
            //</editor-fold>


            //<editor-fold desc="Step 2: calculate sums for each cluster. ">
            // clusterSums[clusterIndex] = (sumL, sumA, sumB) for that cluster
            val clusterSums = Array(centers.size) { Triple(0.0, 0.0, 0.0) }
            //how many points in each cluster
            val clusterSizes = IntArray(centers.size)
            calculateClusterSums(sampledPixels, pointToCluster, clusterSums, clusterSizes)
            //</editor-fold>


            //<editor-fold desc="Step 3: Calculate means value in clusters">
            for (i in centers.indices) {
                centers[i] = if (clusterSizes[i] > 0) LabPoint(
                    clusterSums[i].first / clusterSizes[i],
                    clusterSums[i].second / clusterSizes[i],
                    clusterSums[i].third / clusterSizes[i]
                )
                // If cluster has no points (rare), pick a random pixel as new center
                else sampledPixels[random.nextInt(sampledPixels.size)]
            }
            //</editor-fold>
            iteration++
        }
        return centers
    }


    /**
     * Assign each point to the nearest center.
     * This function updates pointToCluster array.
     *
     * @param points List of all points (pixels)
     * @param centers List of cluster centers
     * @param pointToCluster Array where:
     *        pointToCluster[i] = cluster index of point i
     *
     * @return true if any point changed its cluster
     */
    private fun assignPointsToClusters(
        points: List<LabPoint>,
        centers: List<LabPoint>,
        pointToCluster: IntArray
    ): Boolean {
        // flag: did anything change?
        var changed = false
        // go through all points
        for (i in points.indices) {
            val point = points[i]

            // find nearest center for this point
            val closestCenterIndex =
                centers.indices.minByOrNull { centerIndex ->
                    point.checkLabPointDistance(centers[centerIndex])
                }!!
            // check: did cluster change?
            if (pointToCluster[i] != closestCenterIndex) {
                // update cluster index
                pointToCluster[i] = closestCenterIndex
                // mark change for next iteration
                changed = true
            }
        }

        return changed
    }

    private fun calculateClusterSums(
        sampledPixels: List<LabPoint>,
        pointToCluster: IntArray,
        clusterSums: Array<Triple<Double, Double, Double>>,
        clusterSizes: IntArray
    ) {
        //go through all points
        for (i in sampledPixels.indices) {
            //take cluster[i]
            val currentCluster = pointToCluster[i]
            //take point[i]
            val currentPoint = sampledPixels[i]
            //summarizes value from point[i] to cluster[i]
            clusterSums[currentCluster] =
                Triple(
                    clusterSums[currentCluster].first + currentPoint.l,
                    clusterSums[currentCluster].second + currentPoint.a,
                    clusterSums[currentCluster].third + currentPoint.b
                )
            //add 1 to currentCluster
            clusterSizes[currentCluster]++
        }
    }
}

