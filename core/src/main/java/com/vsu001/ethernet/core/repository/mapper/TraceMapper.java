package com.vsu001.ethernet.core.repository.mapper;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.model.Trace;
import com.vsu001.ethernet.core.model.Trace.Builder;
import com.vsu001.ethernet.core.util.JavaSqlUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

public class TraceMapper implements RowMapper<Trace> {

  /**
   * Implementation of interface used by JdbcTemplate for mapping rows of a ResultSet on a per-row
   * basis.
   * <p>
   * The implementation performs the actual work of mapping each row to a result <code>Trace</code>
   * object. Users do not need to worry about exception handling as SQLExceptions will be caught and
   * handled by the calling JdbcTemplate.
   *
   * @param resultSet The result set to map, which is not null.
   * @param i         The number of the current row.
   * @return The result object, which is of the <code>Trace</code> type for the current row (may be
   * null).
   * @throws SQLException If an SQLException is encountered getting column values.
   */
  @Override
  public Trace mapRow(@NotNull ResultSet resultSet, int i) throws SQLException {
    Builder builder = Trace.newBuilder();
    List<FieldDescriptor> fieldDescriptors = Trace.getDescriptor().getFields();

    for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
      builder.setField(fieldDescriptor, JavaSqlUtil.jSqlToProtoType(fieldDescriptor, resultSet));
    }

    return builder.build();
  }
}
