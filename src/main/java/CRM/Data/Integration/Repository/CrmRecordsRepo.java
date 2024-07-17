package CRM.Data.Integration.Repository;

import CRM.Data.Integration.Entity.CrmRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrmRecordsRepo extends JpaRepository<CrmRecords,Long> {

}
