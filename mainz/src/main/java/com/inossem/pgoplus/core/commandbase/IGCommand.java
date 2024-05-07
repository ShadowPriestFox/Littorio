package com.inossem.pgoplus.core.commandbase;

import java.util.List;

public interface IGCommand {
    String getName();
    List<CommandParameter> getParamsKeys();
    String getConnectorName();
    void init(CommandParams params);
}
