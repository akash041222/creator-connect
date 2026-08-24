package com.creatorconnect.entity;

import jakarta.persistence.*;
import lombok.*;

/** System / security audit trail (admin actions, auth events, data changes). */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(nullable = false)
    private String event; // e.g. "USER_SUSPENDED", "LOGIN_FAILED"

    @Column(length = 2000)
    private String details;

    @Column(name = "ip_address")
    private String ipAddress;
}
