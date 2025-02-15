package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DealPositionAssemblerFactory {

    private Map<DealTypeCode, Supplier<PositionAssembler>> assemblerMap = new HashMap<>();

    public DealPositionAssemblerFactory() {
        initialize();
    }

    private void initialize() {
        assemblerMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalPositionAssembler::new);
        assemblerMap.put(DealTypeCode.FINANCIAL_SWAP, FinancialSwapPositionAssembler::new);
        assemblerMap.put(DealTypeCode.VANILLA_OPTION, VanillaOptionPositionAssembler::new);
    }

    public PositionAssembler newAssembler(DealTypeCode code) {
        return assemblerMap.get(code).get();
    }

    public List<DealPositionSnapshot> assemble(List<DealPosition> positions) {
        ArrayList<DealPositionSnapshot> snapshots = new ArrayList<>();
        for (DealPosition position : positions) {
            PositionAssembler assembler = newAssembler(position.getDealTypeCode());
            snapshots.add(
                    assembler.assemble(
                            position));
        }
        return snapshots;
    }
}
