package io.achord;

import io.netty.buffer.ByteBuf;

import java.time.temporal.Temporal;

import static io.achord.ClickHouseMessageDecoder.readVarUInt;
import static io.achord.ClientMessage.writeStringBinary;
import static java.time.temporal.ChronoField.INSTANT_SECONDS;

/**
 * @author Camelion
 * @since 25.12.2017
 */
enum ColumnType {
    Int8 {
        @Override
        void write(ByteBuf buf, Object val) {
            buf.writeByte(((Number) val).byteValue());
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            column.data = buf.readBytes(rows);
        }
    },
    UInt8 {
        @Override
        void write(ByteBuf buf, Object val) {
            buf.writeByte(((Number) val).byteValue());
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            column.data = buf.readBytes(rows);
        }
    },
    Int32 {
        @Override
        void write(ByteBuf buf, Object val) {
            buf.writeIntLE(((Number) val).intValue());
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            column.data = buf.readBytes(rows * 4);
        }
    },
    UInt32 {
        @Override
        void write(ByteBuf buf, Object val) {
            buf.writeIntLE(((Number) val).intValue());
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            column.data = buf.readBytes(rows * 4);
        }
    },
    Int64 {
        @Override
        void write(ByteBuf buf, Object val) {
            buf.writeLongLE(((Number) val).longValue());
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            column.data = buf.readBytes(rows * 8);
        }
    },
    UInt64 {
        @Override
        void write(ByteBuf buf, Object val) {
            buf.writeLongLE(((Number) val).longValue());
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            column.data = buf.readBytes(rows * 8);
        }
    },
    String {
        @Override
        void write(ByteBuf buf, Object val) {
            String str = (java.lang.String) val;
            writeStringBinary(buf, str);
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            int from = buf.readerIndex();
            buf.markReaderIndex();
            for (int i = 0; i < rows; i++) {
                int strSize = (int) readVarUInt(buf);

                buf.readerIndex(buf.readerIndex() + strSize);
            }

            int to = buf.readerIndex();
            buf.resetReaderIndex();

            column.data = buf.readBytes(to - from);
        }
    },
    DateTime {
        @Override
        void write(ByteBuf buf, Object val) {
            Temporal dateTime = (Temporal) val;
            buf.writeInt((int) dateTime.getLong(INSTANT_SECONDS));
        }

        @Override
        void read(ByteBuf buf, ColumnWithTypeAndName column, int rows) {
            column.data = buf.readBytes(rows * 4);
        }
    };

    abstract void write(ByteBuf buf, Object val);

    abstract void read(ByteBuf buf, ColumnWithTypeAndName column, int rows);
}