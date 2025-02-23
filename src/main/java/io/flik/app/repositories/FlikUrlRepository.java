package io.flik.app.repositories;

import io.flik.app.auth.entities.User;
import io.flik.app.entities.FlikUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface FlikUrlRepository extends JpaRepository<FlikUrl, Integer> {

    public ArrayList<FlikUrl> findAllByUser(User user);
}
