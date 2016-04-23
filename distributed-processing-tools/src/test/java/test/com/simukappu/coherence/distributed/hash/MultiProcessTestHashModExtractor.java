package test.com.simukappu.coherence.distributed.hash;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.simukappu.coherence.distributed.hash.DistributedMemberUtil;
import com.simukappu.coherence.distributed.hash.HashModExtractor;
import com.tangosol.net.CacheFactory;
import com.tangosol.util.Filter;
import com.tangosol.util.extractor.AbstractExtractor;
import com.tangosol.util.filter.EqualsFilter;

import test.com.simukappu.coherence.entity.TestEntity;

/**
 * Test class for HashModExtractor filter running on multi processes. <br>
 * You have to run several number of this test processes depending on
 * BaseHashDistributedProcessingTest.NUM_PROCESSES in the same time.
 * 
 * @author Shota Yamazaki
 */
public class MultiProcessTestHashModExtractor extends BaseMultiProcessHashDistributedProcessingTest {

	/**
	 * Set a coherence member role and ensure cluster to initialize tests
	 */
	@BeforeClass
	public static void ensureCluster() {
		System.setProperty("tangosol.coherence.role", "multi-processes-HashModExtractor-test-member");
		CacheFactory.ensureCluster();
		System.out.println("multi process testing for hash distributed processing");
		System.out.println(" Coherence member role: " + CacheFactory.getCluster().getLocalMember().getRoleName());
		System.out.println();
	}

	/**
	 * Test method for HashModExtractor as key extractor.<br>
	 * This method works on multi process.<br>
	 * Wait for other processes when the number of same role member processes is
	 * not enough.
	 */
	@Test
	public void multiProcessTestHashModExtractorForKey() {
		startTest("multiProcessTestHashModExtractorForKey");

		Map<TestEntity, TestEntity> resultDataMap = new HashMap<>();
		int targetModulo = DistributedMemberUtil.getSequentialIdFromSameRoleMemberSet();
		int numProcesses = DistributedMemberUtil.getSameRoleMembers().size();
		System.out.println(" filtering: targetModulo=" + targetModulo + ", numProcesses=" + numProcesses);

		Filter<TestEntity> hashModFilter = new EqualsFilter<TestEntity, Integer>(
				new HashModExtractor<TestEntity>(AbstractExtractor.KEY, numProcesses), targetModulo);
		Set<TestEntity> targetKeys = testEntityPoolCache.keySet(hashModFilter);
		Map<TestEntity, TestEntity> filteredDataMap = testEntityPoolCache.getAll(targetKeys);
		resultDataMap.putAll(filteredDataMap);
		targetKeys.forEach(key -> testEntityProcessingCountCache.invoke(key, countProcessor));

		checkFilteredResults(targetKeys, filteredDataMap);
		finishTest(resultDataMap, true);
	}

}
