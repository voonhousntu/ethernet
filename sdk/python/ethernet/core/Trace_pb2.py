# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: Trace.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from google.protobuf import timestamp_pb2 as google_dot_protobuf_dot_timestamp__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='Trace.proto',
  package='com.vsu001.ethernet.core',
  syntax='proto3',
  serialized_options=b'\n\036com.vsu001.ethernet.core.modelB\nTraceProtoP\001',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x0bTrace.proto\x12\x18\x63om.vsu001.ethernet.core\x1a\x1fgoogle/protobuf/timestamp.proto\"\xa9\x03\n\x05Trace\x12\x18\n\x10transaction_hash\x18\x01 \x01(\t\x12\x19\n\x11transaction_index\x18\x02 \x01(\x03\x12\x14\n\x0c\x66rom_address\x18\x03 \x01(\t\x12\x12\n\nto_address\x18\x04 \x01(\t\x12\r\n\x05value\x18\x05 \x01(\x01\x12\r\n\x05input\x18\x06 \x01(\t\x12\x0e\n\x06output\x18\x07 \x01(\t\x12\x12\n\ntrace_type\x18\x08 \x01(\t\x12\x11\n\tcall_type\x18\t \x01(\t\x12\x13\n\x0breward_type\x18\n \x01(\t\x12\x0b\n\x03gas\x18\x0b \x01(\x03\x12\x10\n\x08gas_used\x18\x0c \x01(\x03\x12\x11\n\tsubtraces\x18\r \x01(\x03\x12\x15\n\rtrace_address\x18\x0e \x01(\t\x12\r\n\x05\x65rror\x18\x0f \x01(\t\x12\x0e\n\x06status\x18\x10 \x01(\x03\x12\x33\n\x0f\x62lock_timestamp\x18\x11 \x01(\x0b\x32\x1a.google.protobuf.Timestamp\x12\x14\n\x0c\x62lock_number\x18\x12 \x01(\x03\x12\x12\n\nblock_hash\x18\x13 \x01(\t\x12\x10\n\x08trace_id\x18\x14 \x01(\tB.\n\x1e\x63om.vsu001.ethernet.core.modelB\nTraceProtoP\x01\x62\x06proto3'
  ,
  dependencies=[google_dot_protobuf_dot_timestamp__pb2.DESCRIPTOR,])




_TRACE = _descriptor.Descriptor(
  name='Trace',
  full_name='com.vsu001.ethernet.core.Trace',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='transaction_hash', full_name='com.vsu001.ethernet.core.Trace.transaction_hash', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='transaction_index', full_name='com.vsu001.ethernet.core.Trace.transaction_index', index=1,
      number=2, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='from_address', full_name='com.vsu001.ethernet.core.Trace.from_address', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='to_address', full_name='com.vsu001.ethernet.core.Trace.to_address', index=3,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='value', full_name='com.vsu001.ethernet.core.Trace.value', index=4,
      number=5, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='input', full_name='com.vsu001.ethernet.core.Trace.input', index=5,
      number=6, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='output', full_name='com.vsu001.ethernet.core.Trace.output', index=6,
      number=7, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='trace_type', full_name='com.vsu001.ethernet.core.Trace.trace_type', index=7,
      number=8, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='call_type', full_name='com.vsu001.ethernet.core.Trace.call_type', index=8,
      number=9, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='reward_type', full_name='com.vsu001.ethernet.core.Trace.reward_type', index=9,
      number=10, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='gas', full_name='com.vsu001.ethernet.core.Trace.gas', index=10,
      number=11, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='gas_used', full_name='com.vsu001.ethernet.core.Trace.gas_used', index=11,
      number=12, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='subtraces', full_name='com.vsu001.ethernet.core.Trace.subtraces', index=12,
      number=13, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='trace_address', full_name='com.vsu001.ethernet.core.Trace.trace_address', index=13,
      number=14, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='error', full_name='com.vsu001.ethernet.core.Trace.error', index=14,
      number=15, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='status', full_name='com.vsu001.ethernet.core.Trace.status', index=15,
      number=16, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='block_timestamp', full_name='com.vsu001.ethernet.core.Trace.block_timestamp', index=16,
      number=17, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='block_number', full_name='com.vsu001.ethernet.core.Trace.block_number', index=17,
      number=18, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='block_hash', full_name='com.vsu001.ethernet.core.Trace.block_hash', index=18,
      number=19, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='trace_id', full_name='com.vsu001.ethernet.core.Trace.trace_id', index=19,
      number=20, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
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
  serialized_start=75,
  serialized_end=500,
)

_TRACE.fields_by_name['block_timestamp'].message_type = google_dot_protobuf_dot_timestamp__pb2._TIMESTAMP
DESCRIPTOR.message_types_by_name['Trace'] = _TRACE
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Trace = _reflection.GeneratedProtocolMessageType('Trace', (_message.Message,), {
  'DESCRIPTOR' : _TRACE,
  '__module__' : 'Trace_pb2'
  # @@protoc_insertion_point(class_scope:com.vsu001.ethernet.core.Trace)
  })
_sym_db.RegisterMessage(Trace)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)