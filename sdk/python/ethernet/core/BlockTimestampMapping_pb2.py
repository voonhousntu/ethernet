# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: BlockTimestampMapping.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from google.protobuf import timestamp_pb2 as google_dot_protobuf_dot_timestamp__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='BlockTimestampMapping.proto',
  package='com.vsu001.ethernet.core',
  syntax='proto3',
  serialized_options=b'\n\036com.vsu001.ethernet.core.modelB\032BlockTimestampMappingProtoP\001',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x1b\x42lockTimestampMapping.proto\x12\x18\x63om.vsu001.ethernet.core\x1a\x1fgoogle/protobuf/timestamp.proto\"V\n\x15\x42lockTimestampMapping\x12-\n\ttimestamp\x18\x01 \x01(\x0b\x32\x1a.google.protobuf.Timestamp\x12\x0e\n\x06number\x18\x02 \x01(\x03\x42>\n\x1e\x63om.vsu001.ethernet.core.modelB\x1a\x42lockTimestampMappingProtoP\x01\x62\x06proto3'
  ,
  dependencies=[google_dot_protobuf_dot_timestamp__pb2.DESCRIPTOR,])




_BLOCKTIMESTAMPMAPPING = _descriptor.Descriptor(
  name='BlockTimestampMapping',
  full_name='com.vsu001.ethernet.core.BlockTimestampMapping',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='timestamp', full_name='com.vsu001.ethernet.core.BlockTimestampMapping.timestamp', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='number', full_name='com.vsu001.ethernet.core.BlockTimestampMapping.number', index=1,
      number=2, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=90,
  serialized_end=176,
)

_BLOCKTIMESTAMPMAPPING.fields_by_name['timestamp'].message_type = google_dot_protobuf_dot_timestamp__pb2._TIMESTAMP
DESCRIPTOR.message_types_by_name['BlockTimestampMapping'] = _BLOCKTIMESTAMPMAPPING
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

BlockTimestampMapping = _reflection.GeneratedProtocolMessageType('BlockTimestampMapping', (_message.Message,), {
  'DESCRIPTOR' : _BLOCKTIMESTAMPMAPPING,
  '__module__' : 'BlockTimestampMapping_pb2'
  # @@protoc_insertion_point(class_scope:com.vsu001.ethernet.core.BlockTimestampMapping)
  })
_sym_db.RegisterMessage(BlockTimestampMapping)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)
