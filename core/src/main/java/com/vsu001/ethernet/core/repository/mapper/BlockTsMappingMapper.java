package com.vsu001.ethernet.core.repository.mapper;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.model.BlockTimestampMapping.Builder;
import com.vsu001.ethernet.core.util.JavaSqlUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

public class BlockTsMappingMapper implements RowMapper<BlockTimestampMapping> {

  @Override
  public BlockTimestampMapping mapRow(@NotNull ResultSet resultSet, int i) throws SQLException {
    Builder builder = BlockTimestampMapping.newBuilder();
    List<FieldDescriptor> fieldDescriptors = BlockTimestampMapping.getDescriptor().getFields();

    for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
      builder.setField(fieldDescriptor, JavaSqlUtil.jSqlToProtoType(fieldDescriptor, resultSet));
    }

    return builder.build();
  }

}
