package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import io.grpc.stub.StreamObserver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@GrpcService
public class CoreServiceImpl extends CoreServiceGrpc.CoreServiceImplBase {

  private final JdbcTemplate jdbcTemplate;

  public CoreServiceImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void updateBlockTsMapping(
      BlockTsMappingUpdateRequest request,
      StreamObserver<BlockTsMappingUpdateResponse> responseObserver) {

    try {
      log.info("Updating `block_timestamp_mapping`");
      // For debugging
      TableResult tableResult = BigQueryUtil.query(
          BlockTimestampMapping.getDescriptor(),
          "blocks",
          "timestamp < '2015-07-30 15:46:52'"
      );

      // TODO: Fetch the largest timestamp from the table
//      TableResult tableResult = BigQueryUtil.query(
//          BlockTimestampMapping.getDescriptor(),
//          "blocks".
//              "timestamp > ''"
//      );
      long rowsFetched = tableResult.getTotalRows();
      log.info("Rows fetched: [{}]", rowsFetched);

      BlockTsMappingUpdateResponse reply = BlockTsMappingUpdateResponse.newBuilder()
          .setNumber(-1L)
          .setTimestamp(Timestamp.newBuilder().build())
          .build();

      // Write to hive table
      saveBatch(tableResult);

      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    } catch (InterruptedException e) {
      responseObserver.onError(e.getCause());
      e.printStackTrace();
    }

  }

  private void saveBatch(TableResult tableResult) {
    final String query =
        "INSERT INTO ethernet.block_timestamp_mapping partition(`number`) VALUES (?, ?)";
    final String nonStrictPartition =
        "SET hive.exec.dynamic.partition.mode=nonstrict";

    // Enable dynamic partition in non-strict mode
    jdbcTemplate.execute(nonStrictPartition);

    boolean isFirstPage = true;
    do {
      if (!isFirstPage) {
        tableResult = tableResult.getNextPage();
      }

      List<FieldValueList> rowList = new ArrayList<>();
      Iterable<FieldValueList> rowIterable = tableResult.getValues();
      rowIterable.forEach(rowList::add);

      jdbcTemplate.batchUpdate(
          query,
          new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
              FieldValueList tableRow = rowList.get(i);
              ps.setString(1, tableRow.get("timestamp").getStringValue());
              ps.setString(2, tableRow.get("number").getStringValue());
            }

            @Override
            public int getBatchSize() {
              return rowList.size();
            }
          }
      );
      isFirstPage = false;
    } while (tableResult.hasNextPage());
  }

}
