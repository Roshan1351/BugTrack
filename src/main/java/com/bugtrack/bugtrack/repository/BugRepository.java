package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.*;

public interface BugRepository extends JpaRepository<Bug, Integer>  {
    List<Bug> findByProject_ProjectId(Integer ProjectId);

    List<Bug> findByAssignedTo_UserId(Integer userId);

    List<Bug> findByRaisedBy_UserId(Integer userId);

    List<Bug> findByProject_ProjectIdAndStatus_StatusName(Integer ProjectId, String StatusName);


//    special features for duplicate detection
    @Query("select b from Bug b where b.project.projectId = :projectId And b.status.statusName Not In('Closed', 'Rejected')")
    List<Bug> findOpenBugsByProject(@Param("projectId") Integer projectId);

//work load balance assignment using sql query
    @Query("SELECT b.assignedTo.userId, COUNT(b) as bugCount " +
            "FROM Bug b " +
            "WHERE b.project.projectId = :projectId " +
            "AND b.assignedTo IS NOT NULL " +
            "AND b.status.statusName NOT IN ('Closed', 'Rejected') " +
            "GROUP BY b.assignedTo.userId " +
            "ORDER BY bugCount ASC")
    List<Object[]> findDeveloperWorkloadByProject(
            @Param("projectId") Integer projectId
    );
  //for @Scheduled job and sla engine
    @Query("select b from Bug b where b.dueDate is not NULL and b.dueDate < :now and b.status.statusName not in ('Closed', 'Rejected', 'Resolved')")
    List<Bug> findOverdueBugs(@Param("now")LocalDateTime now);


    //for dashboard query
    long countByProject_ProjectIdAndStatus_StatusName(Integer projectId, String statusName);

    @Query("select count(b) from Bug b where b.assignedTo.userId = :developerId and b.status.statusName = 'Reopened'")
    long countReoepenedBugsByDeveloper(@Param("developerId") Integer developerId);


    // Total bugs assigned to a developer (sabhi statuses)
    long countByAssignedTo_UserId(Integer userId);

    // Active bugs — jo abhi resolve/close nahi hue
    @Query("SELECT COUNT(b) FROM Bug b WHERE b.assignedTo.userId = :userId " +
            "AND b.status.statusName NOT IN ('Resolved', 'Closed', 'Rejected')")
    long countActiveBugsByDeveloper(@Param("userId") Integer userId);

    // Completed bugs — Resolved + Closed
    @Query("SELECT COUNT(b) FROM Bug b WHERE b.assignedTo.userId = :userId " +
            "AND b.status.statusName IN ('Resolved', 'Closed')")
    long countCompletedBugsByDeveloper(@Param("userId") Integer userId);

    // All users workload ek saath — JPQL bulk query
    @Query("SELECT b.assignedTo.userId, " +
            "COUNT(b), " +
            "SUM(CASE WHEN b.status.statusName NOT IN ('Resolved','Closed','Rejected') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN b.status.statusName IN ('Resolved','Closed') THEN 1 ELSE 0 END) " +
            "FROM Bug b WHERE b.assignedTo IS NOT NULL GROUP BY b.assignedTo.userId")
    List<Object[]> findAllUserWorkloadStats();
}
