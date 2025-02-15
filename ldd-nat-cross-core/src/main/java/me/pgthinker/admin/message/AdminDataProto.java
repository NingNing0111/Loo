// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: admin_data.proto

// Protobuf Java Version: 3.25.3
package me.pgthinker.admin.message;

public final class AdminDataProto {
  private AdminDataProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface AdminDataOrBuilder extends
      // @@protoc_insertion_point(interface_extends:me.pgthinker.admin.AdminData)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 时间戳
     * </pre>
     *
     * <code>.google.protobuf.Timestamp timestamp = 1;</code>
     * @return Whether the timestamp field is set.
     */
    boolean hasTimestamp();
    /**
     * <pre>
     * 时间戳
     * </pre>
     *
     * <code>.google.protobuf.Timestamp timestamp = 1;</code>
     * @return The timestamp.
     */
    com.google.protobuf.Timestamp getTimestamp();
    /**
     * <pre>
     * 时间戳
     * </pre>
     *
     * <code>.google.protobuf.Timestamp timestamp = 1;</code>
     */
    com.google.protobuf.TimestampOrBuilder getTimestampOrBuilder();

    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    int getMetaDataCount();
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    boolean containsMetaData(
        java.lang.String key);
    /**
     * Use {@link #getMetaDataMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, java.lang.String>
    getMetaData();
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    java.util.Map<java.lang.String, java.lang.String>
    getMetaDataMap();
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    /* nullable */
java.lang.String getMetaDataOrDefault(
        java.lang.String key,
        /* nullable */
java.lang.String defaultValue);
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    java.lang.String getMetaDataOrThrow(
        java.lang.String key);
  }
  /**
   * <pre>
   * 传输数据的消息定义
   * </pre>
   *
   * Protobuf type {@code me.pgthinker.admin.AdminData}
   */
  public static final class AdminData extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:me.pgthinker.admin.AdminData)
      AdminDataOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use AdminData.newBuilder() to construct.
    private AdminData(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private AdminData() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new AdminData();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return me.pgthinker.admin.message.AdminDataProto.internal_static_me_pgthinker_admin_AdminData_descriptor;
    }

    @SuppressWarnings({"rawtypes"})
    @java.lang.Override
    protected com.google.protobuf.MapFieldReflectionAccessor internalGetMapFieldReflection(
        int number) {
      switch (number) {
        case 2:
          return internalGetMetaData();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return me.pgthinker.admin.message.AdminDataProto.internal_static_me_pgthinker_admin_AdminData_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              me.pgthinker.admin.message.AdminDataProto.AdminData.class, me.pgthinker.admin.message.AdminDataProto.AdminData.Builder.class);
    }

    private int bitField0_;
    public static final int TIMESTAMP_FIELD_NUMBER = 1;
    private com.google.protobuf.Timestamp timestamp_;
    /**
     * <pre>
     * 时间戳
     * </pre>
     *
     * <code>.google.protobuf.Timestamp timestamp = 1;</code>
     * @return Whether the timestamp field is set.
     */
    @java.lang.Override
    public boolean hasTimestamp() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <pre>
     * 时间戳
     * </pre>
     *
     * <code>.google.protobuf.Timestamp timestamp = 1;</code>
     * @return The timestamp.
     */
    @java.lang.Override
    public com.google.protobuf.Timestamp getTimestamp() {
      return timestamp_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : timestamp_;
    }
    /**
     * <pre>
     * 时间戳
     * </pre>
     *
     * <code>.google.protobuf.Timestamp timestamp = 1;</code>
     */
    @java.lang.Override
    public com.google.protobuf.TimestampOrBuilder getTimestampOrBuilder() {
      return timestamp_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : timestamp_;
    }

    public static final int METADATA_FIELD_NUMBER = 2;
    private static final class MetaDataDefaultEntryHolder {
      static final com.google.protobuf.MapEntry<
          java.lang.String, java.lang.String> defaultEntry =
              com.google.protobuf.MapEntry
              .<java.lang.String, java.lang.String>newDefaultInstance(
                  me.pgthinker.admin.message.AdminDataProto.internal_static_me_pgthinker_admin_AdminData_MetaDataEntry_descriptor, 
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "");
    }
    @SuppressWarnings("serial")
    private com.google.protobuf.MapField<
        java.lang.String, java.lang.String> metaData_;
    private com.google.protobuf.MapField<java.lang.String, java.lang.String>
    internalGetMetaData() {
      if (metaData_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            MetaDataDefaultEntryHolder.defaultEntry);
      }
      return metaData_;
    }
    public int getMetaDataCount() {
      return internalGetMetaData().getMap().size();
    }
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    @java.lang.Override
    public boolean containsMetaData(
        java.lang.String key) {
      if (key == null) { throw new NullPointerException("map key"); }
      return internalGetMetaData().getMap().containsKey(key);
    }
    /**
     * Use {@link #getMetaDataMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, java.lang.String> getMetaData() {
      return getMetaDataMap();
    }
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    @java.lang.Override
    public java.util.Map<java.lang.String, java.lang.String> getMetaDataMap() {
      return internalGetMetaData().getMap();
    }
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    @java.lang.Override
    public /* nullable */
java.lang.String getMetaDataOrDefault(
        java.lang.String key,
        /* nullable */
java.lang.String defaultValue) {
      if (key == null) { throw new NullPointerException("map key"); }
      java.util.Map<java.lang.String, java.lang.String> map =
          internalGetMetaData().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <pre>
     * 元数据
     * </pre>
     *
     * <code>map&lt;string, string&gt; metaData = 2;</code>
     */
    @java.lang.Override
    public java.lang.String getMetaDataOrThrow(
        java.lang.String key) {
      if (key == null) { throw new NullPointerException("map key"); }
      java.util.Map<java.lang.String, java.lang.String> map =
          internalGetMetaData().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeMessage(1, getTimestamp());
      }
      com.google.protobuf.GeneratedMessageV3
        .serializeStringMapTo(
          output,
          internalGetMetaData(),
          MetaDataDefaultEntryHolder.defaultEntry,
          2);
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, getTimestamp());
      }
      for (java.util.Map.Entry<java.lang.String, java.lang.String> entry
           : internalGetMetaData().getMap().entrySet()) {
        com.google.protobuf.MapEntry<java.lang.String, java.lang.String>
        metaData__ = MetaDataDefaultEntryHolder.defaultEntry.newBuilderForType()
            .setKey(entry.getKey())
            .setValue(entry.getValue())
            .build();
        size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(2, metaData__);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof me.pgthinker.admin.message.AdminDataProto.AdminData)) {
        return super.equals(obj);
      }
      me.pgthinker.admin.message.AdminDataProto.AdminData other = (me.pgthinker.admin.message.AdminDataProto.AdminData) obj;

      if (hasTimestamp() != other.hasTimestamp()) return false;
      if (hasTimestamp()) {
        if (!getTimestamp()
            .equals(other.getTimestamp())) return false;
      }
      if (!internalGetMetaData().equals(
          other.internalGetMetaData())) return false;
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasTimestamp()) {
        hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
        hash = (53 * hash) + getTimestamp().hashCode();
      }
      if (!internalGetMetaData().getMap().isEmpty()) {
        hash = (37 * hash) + METADATA_FIELD_NUMBER;
        hash = (53 * hash) + internalGetMetaData().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static me.pgthinker.admin.message.AdminDataProto.AdminData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(me.pgthinker.admin.message.AdminDataProto.AdminData prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     * 传输数据的消息定义
     * </pre>
     *
     * Protobuf type {@code me.pgthinker.admin.AdminData}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:me.pgthinker.admin.AdminData)
        me.pgthinker.admin.message.AdminDataProto.AdminDataOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return me.pgthinker.admin.message.AdminDataProto.internal_static_me_pgthinker_admin_AdminData_descriptor;
      }

      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapFieldReflectionAccessor internalGetMapFieldReflection(
          int number) {
        switch (number) {
          case 2:
            return internalGetMetaData();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapFieldReflectionAccessor internalGetMutableMapFieldReflection(
          int number) {
        switch (number) {
          case 2:
            return internalGetMutableMetaData();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return me.pgthinker.admin.message.AdminDataProto.internal_static_me_pgthinker_admin_AdminData_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                me.pgthinker.admin.message.AdminDataProto.AdminData.class, me.pgthinker.admin.message.AdminDataProto.AdminData.Builder.class);
      }

      // Construct using me.pgthinker.admin.message.AdminDataProto.AdminData.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getTimestampFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        timestamp_ = null;
        if (timestampBuilder_ != null) {
          timestampBuilder_.dispose();
          timestampBuilder_ = null;
        }
        internalGetMutableMetaData().clear();
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return me.pgthinker.admin.message.AdminDataProto.internal_static_me_pgthinker_admin_AdminData_descriptor;
      }

      @java.lang.Override
      public me.pgthinker.admin.message.AdminDataProto.AdminData getDefaultInstanceForType() {
        return me.pgthinker.admin.message.AdminDataProto.AdminData.getDefaultInstance();
      }

      @java.lang.Override
      public me.pgthinker.admin.message.AdminDataProto.AdminData build() {
        me.pgthinker.admin.message.AdminDataProto.AdminData result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public me.pgthinker.admin.message.AdminDataProto.AdminData buildPartial() {
        me.pgthinker.admin.message.AdminDataProto.AdminData result = new me.pgthinker.admin.message.AdminDataProto.AdminData(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(me.pgthinker.admin.message.AdminDataProto.AdminData result) {
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.timestamp_ = timestampBuilder_ == null
              ? timestamp_
              : timestampBuilder_.build();
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.metaData_ = internalGetMetaData();
          result.metaData_.makeImmutable();
        }
        result.bitField0_ |= to_bitField0_;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof me.pgthinker.admin.message.AdminDataProto.AdminData) {
          return mergeFrom((me.pgthinker.admin.message.AdminDataProto.AdminData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(me.pgthinker.admin.message.AdminDataProto.AdminData other) {
        if (other == me.pgthinker.admin.message.AdminDataProto.AdminData.getDefaultInstance()) return this;
        if (other.hasTimestamp()) {
          mergeTimestamp(other.getTimestamp());
        }
        internalGetMutableMetaData().mergeFrom(
            other.internalGetMetaData());
        bitField0_ |= 0x00000002;
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {
                input.readMessage(
                    getTimestampFieldBuilder().getBuilder(),
                    extensionRegistry);
                bitField0_ |= 0x00000001;
                break;
              } // case 10
              case 18: {
                com.google.protobuf.MapEntry<java.lang.String, java.lang.String>
                metaData__ = input.readMessage(
                    MetaDataDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
                internalGetMutableMetaData().getMutableMap().put(
                    metaData__.getKey(), metaData__.getValue());
                bitField0_ |= 0x00000002;
                break;
              } // case 18
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.Timestamp timestamp_;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> timestampBuilder_;
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       * @return Whether the timestamp field is set.
       */
      public boolean hasTimestamp() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       * @return The timestamp.
       */
      public com.google.protobuf.Timestamp getTimestamp() {
        if (timestampBuilder_ == null) {
          return timestamp_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : timestamp_;
        } else {
          return timestampBuilder_.getMessage();
        }
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       */
      public Builder setTimestamp(com.google.protobuf.Timestamp value) {
        if (timestampBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          timestamp_ = value;
        } else {
          timestampBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       */
      public Builder setTimestamp(
          com.google.protobuf.Timestamp.Builder builderForValue) {
        if (timestampBuilder_ == null) {
          timestamp_ = builderForValue.build();
        } else {
          timestampBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       */
      public Builder mergeTimestamp(com.google.protobuf.Timestamp value) {
        if (timestampBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0) &&
            timestamp_ != null &&
            timestamp_ != com.google.protobuf.Timestamp.getDefaultInstance()) {
            getTimestampBuilder().mergeFrom(value);
          } else {
            timestamp_ = value;
          }
        } else {
          timestampBuilder_.mergeFrom(value);
        }
        if (timestamp_ != null) {
          bitField0_ |= 0x00000001;
          onChanged();
        }
        return this;
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       */
      public Builder clearTimestamp() {
        bitField0_ = (bitField0_ & ~0x00000001);
        timestamp_ = null;
        if (timestampBuilder_ != null) {
          timestampBuilder_.dispose();
          timestampBuilder_ = null;
        }
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       */
      public com.google.protobuf.Timestamp.Builder getTimestampBuilder() {
        bitField0_ |= 0x00000001;
        onChanged();
        return getTimestampFieldBuilder().getBuilder();
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       */
      public com.google.protobuf.TimestampOrBuilder getTimestampOrBuilder() {
        if (timestampBuilder_ != null) {
          return timestampBuilder_.getMessageOrBuilder();
        } else {
          return timestamp_ == null ?
              com.google.protobuf.Timestamp.getDefaultInstance() : timestamp_;
        }
      }
      /**
       * <pre>
       * 时间戳
       * </pre>
       *
       * <code>.google.protobuf.Timestamp timestamp = 1;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> 
          getTimestampFieldBuilder() {
        if (timestampBuilder_ == null) {
          timestampBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder>(
                  getTimestamp(),
                  getParentForChildren(),
                  isClean());
          timestamp_ = null;
        }
        return timestampBuilder_;
      }

      private com.google.protobuf.MapField<
          java.lang.String, java.lang.String> metaData_;
      private com.google.protobuf.MapField<java.lang.String, java.lang.String>
          internalGetMetaData() {
        if (metaData_ == null) {
          return com.google.protobuf.MapField.emptyMapField(
              MetaDataDefaultEntryHolder.defaultEntry);
        }
        return metaData_;
      }
      private com.google.protobuf.MapField<java.lang.String, java.lang.String>
          internalGetMutableMetaData() {
        if (metaData_ == null) {
          metaData_ = com.google.protobuf.MapField.newMapField(
              MetaDataDefaultEntryHolder.defaultEntry);
        }
        if (!metaData_.isMutable()) {
          metaData_ = metaData_.copy();
        }
        bitField0_ |= 0x00000002;
        onChanged();
        return metaData_;
      }
      public int getMetaDataCount() {
        return internalGetMetaData().getMap().size();
      }
      /**
       * <pre>
       * 元数据
       * </pre>
       *
       * <code>map&lt;string, string&gt; metaData = 2;</code>
       */
      @java.lang.Override
      public boolean containsMetaData(
          java.lang.String key) {
        if (key == null) { throw new NullPointerException("map key"); }
        return internalGetMetaData().getMap().containsKey(key);
      }
      /**
       * Use {@link #getMetaDataMap()} instead.
       */
      @java.lang.Override
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, java.lang.String> getMetaData() {
        return getMetaDataMap();
      }
      /**
       * <pre>
       * 元数据
       * </pre>
       *
       * <code>map&lt;string, string&gt; metaData = 2;</code>
       */
      @java.lang.Override
      public java.util.Map<java.lang.String, java.lang.String> getMetaDataMap() {
        return internalGetMetaData().getMap();
      }
      /**
       * <pre>
       * 元数据
       * </pre>
       *
       * <code>map&lt;string, string&gt; metaData = 2;</code>
       */
      @java.lang.Override
      public /* nullable */
java.lang.String getMetaDataOrDefault(
          java.lang.String key,
          /* nullable */
java.lang.String defaultValue) {
        if (key == null) { throw new NullPointerException("map key"); }
        java.util.Map<java.lang.String, java.lang.String> map =
            internalGetMetaData().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <pre>
       * 元数据
       * </pre>
       *
       * <code>map&lt;string, string&gt; metaData = 2;</code>
       */
      @java.lang.Override
      public java.lang.String getMetaDataOrThrow(
          java.lang.String key) {
        if (key == null) { throw new NullPointerException("map key"); }
        java.util.Map<java.lang.String, java.lang.String> map =
            internalGetMetaData().getMap();
        if (!map.containsKey(key)) {
          throw new java.lang.IllegalArgumentException();
        }
        return map.get(key);
      }
      public Builder clearMetaData() {
        bitField0_ = (bitField0_ & ~0x00000002);
        internalGetMutableMetaData().getMutableMap()
            .clear();
        return this;
      }
      /**
       * <pre>
       * 元数据
       * </pre>
       *
       * <code>map&lt;string, string&gt; metaData = 2;</code>
       */
      public Builder removeMetaData(
          java.lang.String key) {
        if (key == null) { throw new NullPointerException("map key"); }
        internalGetMutableMetaData().getMutableMap()
            .remove(key);
        return this;
      }
      /**
       * Use alternate mutation accessors instead.
       */
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, java.lang.String>
          getMutableMetaData() {
        bitField0_ |= 0x00000002;
        return internalGetMutableMetaData().getMutableMap();
      }
      /**
       * <pre>
       * 元数据
       * </pre>
       *
       * <code>map&lt;string, string&gt; metaData = 2;</code>
       */
      public Builder putMetaData(
          java.lang.String key,
          java.lang.String value) {
        if (key == null) { throw new NullPointerException("map key"); }
        if (value == null) { throw new NullPointerException("map value"); }
        internalGetMutableMetaData().getMutableMap()
            .put(key, value);
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <pre>
       * 元数据
       * </pre>
       *
       * <code>map&lt;string, string&gt; metaData = 2;</code>
       */
      public Builder putAllMetaData(
          java.util.Map<java.lang.String, java.lang.String> values) {
        internalGetMutableMetaData().getMutableMap()
            .putAll(values);
        bitField0_ |= 0x00000002;
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:me.pgthinker.admin.AdminData)
    }

    // @@protoc_insertion_point(class_scope:me.pgthinker.admin.AdminData)
    private static final me.pgthinker.admin.message.AdminDataProto.AdminData DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new me.pgthinker.admin.message.AdminDataProto.AdminData();
    }

    public static me.pgthinker.admin.message.AdminDataProto.AdminData getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<AdminData>
        PARSER = new com.google.protobuf.AbstractParser<AdminData>() {
      @java.lang.Override
      public AdminData parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<AdminData> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<AdminData> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public me.pgthinker.admin.message.AdminDataProto.AdminData getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_me_pgthinker_admin_AdminData_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_me_pgthinker_admin_AdminData_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_me_pgthinker_admin_AdminData_MetaDataEntry_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_me_pgthinker_admin_AdminData_MetaDataEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\020admin_data.proto\022\022me.pgthinker.admin\032\037" +
      "google/protobuf/timestamp.proto\"\252\001\n\tAdmi" +
      "nData\022-\n\ttimestamp\030\001 \001(\0132\032.google.protob" +
      "uf.Timestamp\022=\n\010metaData\030\002 \003(\0132+.me.pgth" +
      "inker.admin.AdminData.MetaDataEntry\032/\n\rM" +
      "etaDataEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\t" +
      ":\0028\001B.\n\032me.pgthinker.admin.messageB\016Admi" +
      "nDataProtoP\000b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_me_pgthinker_admin_AdminData_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_me_pgthinker_admin_AdminData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_me_pgthinker_admin_AdminData_descriptor,
        new java.lang.String[] { "Timestamp", "MetaData", });
    internal_static_me_pgthinker_admin_AdminData_MetaDataEntry_descriptor =
      internal_static_me_pgthinker_admin_AdminData_descriptor.getNestedTypes().get(0);
    internal_static_me_pgthinker_admin_AdminData_MetaDataEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_me_pgthinker_admin_AdminData_MetaDataEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
