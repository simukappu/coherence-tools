package com.simukappu.coherence.distributed.hash;

import java.util.Set;
import java.util.stream.Collectors;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Member;

/**
 * Utility class to manage the number of distributed members and distributed id
 * for each processes using Coherence member role.
 * 
 * @author Shota Yamazaki
 *
 */
public class DistributedMemberUtil {

	/**
	 * Private constructor to make this class singleton
	 */
	private DistributedMemberUtil() {
	}

	/**
	 * Get a set of same role coherence members
	 * 
	 * @return Set of same role coherence members
	 */
	public static final Set<Member> getSameRoleMembers() {
		// Get role of this process
		String localMemberRole = CacheFactory.getCluster().getLocalMember().getRoleName();

		// Get member set from Coherence cluster
		@SuppressWarnings("unchecked")
		Set<Member> members = CacheFactory.getCluster().getMemberSet();

		// Filter members with same role and return as set of members
		Set<Member> sameRoleMembers = members.stream().filter(member -> member.getRoleName().equals(localMemberRole))
				.collect(Collectors.toSet());
		return sameRoleMembers;
	}

	/**
	 * Get a unique sequential id from member set
	 * 
	 * @param memberSet
	 *            Coherence member set
	 * @return Unique sequential id
	 */
	public static final int getSequentialIdFromMemberSet(Set<Member> memberSet) {
		// Get member id of this process
		int localMemberId = CacheFactory.getCluster().getLocalMember().getId();

		int seqId = (int) memberSet.stream().filter(member -> member.getId() < localMemberId).count();
		return seqId;
	}

	/**
	 * Get a unique sequential id from same role coherence members
	 * 
	 * @return Unique sequential id
	 */
	public static final int getSequentialIdFromSameRoleMemberSet() {
		return getSequentialIdFromMemberSet(getSameRoleMembers());
	}

}
