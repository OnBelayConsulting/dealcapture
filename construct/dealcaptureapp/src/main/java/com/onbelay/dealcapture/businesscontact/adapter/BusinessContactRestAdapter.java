package com.onbelay.dealcapture.businesscontact.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshotCollection;

public interface BusinessContactRestAdapter {

    TransactionResult save(BusinessContactSnapshot snapshot);

    BusinessContactSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);



    BusinessContactSnapshot load(EntityId id);
}
