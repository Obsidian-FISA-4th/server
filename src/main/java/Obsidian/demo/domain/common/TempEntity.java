package Obsidian.demo.domain.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "temp")
public class TempEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type; // "FOLDER" or "FILE"

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TempEntity parent;
}