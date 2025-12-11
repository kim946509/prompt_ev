package kr.co.ansandy.prompt_ev.repository;

import kr.co.ansandy.prompt_ev.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
