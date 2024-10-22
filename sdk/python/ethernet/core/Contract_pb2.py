# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: Contract.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from google.protobuf import timestamp_pb2 as google_dot_protobuf_dot_timestamp__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='Contract.proto',
  package='com.vsu001.ethernet.core',
  syntax='proto3',
  serialized_options=b'\n\036com.vsu001.ethernet.core.modelB\rContractProtoP\001',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x0e\x43ontract.proto\x12\x18\x63om.vsu001.ethernet.core\x1a\x1fgoogle/protobuf/timestamp.proto\"\xcd\x01\n\x08\x43ontract\x12\x0f\n\x07\x61\x64\x64ress\x18\x01 \x01(\t\x12\x10\n\x08\x62ytecode\x18\x02 \x01(\t\x12\x1a\n\x12\x66unction_sighashes\x18\x03 \x01(\t\x12\x10\n\x08is_erc20\x18\x04 \x01(\x08\x12\x11\n\tis_erc721\x18\x05 \x01(\x08\x12\x33\n\x0f\x62lock_timestamp\x18\x06 \x01(\x0b\x32\x1a.google.protobuf.Timestamp\x12\x14\n\x0c\x62lock_number\x18\x07 \x01(\x03\x12\x12\n\nblock_hash\x18\x08 \x01(\tB1\n\x1e\x63om.vsu001.ethernet.core.modelB\rContractProtoP\x01\x62\x06proto3'
  ,
  dependencies=[google_dot_protobuf_dot_timestamp__pb2.DESCRIPTOR,])




_CONTRACT = _descriptor.Descriptor(
  name='Contract',
  full_name='com.vsu001.ethernet.core.Contract',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='address', full_name='com.vsu001.ethernet.core.Contract.address', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='bytecode', full_name='com.vsu001.ethernet.core.Contract.bytecode', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='function_sighashes', full_name='com.vsu001.ethernet.core.Contract.function_sighashes', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='is_erc20', full_name='com.vsu001.ethernet.core.Contract.is_erc20', index=3,
      number=4, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='is_erc721', full_name='com.vsu001.ethernet.core.Contract.is_erc721', index=4,
      number=5, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='block_timestamp', full_name='com.vsu001.ethernet.core.Contract.block_timestamp', index=5,
      number=6, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='block_number', full_name='com.vsu001.ethernet.core.Contract.block_number', index=6,
      number=7, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='block_hash', full_name='com.vsu001.ethernet.core.Contract.block_hash', index=7,
      number=8, type=9, cpp_type=9, label=1,
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
  serialized_start=78,
  serialized_end=283,
)

_CONTRACT.fields_by_name['block_timestamp'].message_type = google_dot_protobuf_dot_timestamp__pb2._TIMESTAMP
DESCRIPTOR.message_types_by_name['Contract'] = _CONTRACT
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Contract = _reflection.GeneratedProtocolMessageType('Contract', (_message.Message,), {
  'DESCRIPTOR' : _CONTRACT,
  '__module__' : 'Contract_pb2'
  # @@protoc_insertion_point(class_scope:com.vsu001.ethernet.core.Contract)
  })
_sym_db.RegisterMessage(Contract)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)
