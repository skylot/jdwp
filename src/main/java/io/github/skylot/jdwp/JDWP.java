/**
 * This file based on
 * https://docs.oracle.com/en/java/javase/15/docs/specs/jdwp/jdwp-spec.html
 * and
 * https://docs.oracle.com/javase/15/docs/platform/jpda/jdwp/jdwp-protocol.html
 */

package io.github.skylot.jdwp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "SameParameterValue"})
public class JDWP {
	public static final int PACKET_HEADER_SIZE = 11;
	private static final byte[] HANDSHAKE_BYTES = "JDWP-Handshake".getBytes();

	private final ValueTag valueTag;
	/* Command Sets */
	private final VirtualMachine virtualMachine;
	private final ReferenceType referenceType;
	private final ClassType classType;
	private final ArrayType arrayType;
	private final InterfaceType interfaceType;
	private final Method method;
	private final Field field;
	private final ObjectReference objectReference;
	private final StringReference stringReference;
	private final ThreadReference threadReference;
	private final ThreadGroupReference threadGroupReference;
	private final ArrayReference arrayReference;
	private final ClassLoaderReference classLoaderReference;
	private final EventRequest eventRequest;
	private final StackFrame stackFrame;
	private final ClassObjectReference classObjectReference;
	private final Event event;

	// private static final JdwpByte JdwpByte;
	// private static final JdwpBoolean JdwpBoolean;
	// private static final JdwpInt JdwpInt;
	// private static final JdwpLong JdwpLong;
	private final JdwpObjectID mObjectID;
	private final JdwpTaggedobjectID mTaggedobjectID;
	private final JdwpThreadID mThreadID;
	private final JdwpThreadGroupID mThreadGroupID;
	private final JdwpStringID mStringID;
	private final JdwpModuleID mModuleID;
	private final JdwpClassLoaderID mClassLoaderID;
	private final JdwpClassObjectID mClassObjectID;
	private final JdwpArrayID mArrayID;
	private final JdwpReferenceTypeID mReferenceTypeID;
	private final JdwpClassID mClassID;
	private final JdwpInterfaceID mInterfaceID;
	private final JdwpArrayTypeID mArrayTypeID;
	private final JdwpMethodID mMethodID;
	private final JdwpFieldID mFieldID;
	private final JdwpFrameID mFrameID;
	private final JdwpLocation mLocation;
	// private static final JdwpString JdwpString;
	private final JdwpValue mValue;
	private final JdwpUntaggedvalue mUntaggedvalue;
	private final JdwpArrayregion mArrayregion;

	public JDWP(IDSizes.IDSizesReplyData sizes) {
		this(sizes.fieldIDSize, sizes.methodIDSize, sizes.objectIDSize,
				sizes.referenceTypeIDSize, sizes.frameIDSize);
	}

	private JDWP(int fieldIDSize, int methodIDSize,
				 int objectIDSize, int referenceTypeIDSize, int frameIDSize) {
		this.valueTag = new ValueTag(objectIDSize);
		virtualMachine = new VirtualMachine();
		referenceType = new ReferenceType();
		classType = new ClassType();
		arrayType = new ArrayType();
		interfaceType = new InterfaceType();
		method = new Method();
		field = new Field();
		objectReference = new ObjectReference();
		stringReference = new StringReference();
		threadReference = new ThreadReference();
		threadGroupReference = new ThreadGroupReference();
		arrayReference = new ArrayReference();
		classLoaderReference = new ClassLoaderReference();
		eventRequest = new EventRequest();
		stackFrame = new StackFrame();
		classObjectReference = new ClassObjectReference();
		event = new Event();

		mObjectID = new JdwpObjectID(objectIDSize);
		mTaggedobjectID = new JdwpTaggedobjectID(objectIDSize);
		mThreadID = new JdwpThreadID(objectIDSize);
		mThreadGroupID = new JdwpThreadGroupID(objectIDSize);
		mStringID = new JdwpStringID(objectIDSize);
		mModuleID = new JdwpModuleID(objectIDSize);
		mClassLoaderID = new JdwpClassLoaderID(objectIDSize);
		mClassObjectID = new JdwpClassObjectID(objectIDSize);
		mArrayID = new JdwpArrayID(objectIDSize);
		mReferenceTypeID = new JdwpReferenceTypeID(referenceTypeIDSize);
		mClassID = new JdwpClassID(referenceTypeIDSize);
		mInterfaceID = new JdwpInterfaceID(referenceTypeIDSize);
		mArrayTypeID = new JdwpArrayTypeID(referenceTypeIDSize);
		mMethodID = new JdwpMethodID(methodIDSize);
		mFieldID = new JdwpFieldID(fieldIDSize);
		mFrameID = new JdwpFrameID(frameIDSize);
		mLocation = new JdwpLocation(referenceTypeIDSize, methodIDSize);
		mValue = new JdwpValue();
		mUntaggedvalue = new JdwpUntaggedvalue();
		mArrayregion = new JdwpArrayregion();
	}

	public VirtualMachine virtualMachine() {
		return virtualMachine;
	}

	public ReferenceType referenceType() {
		return referenceType;
	}

	public ClassType classType() {
		return classType;
	}

	public ArrayType arrayType() {
		return arrayType;
	}

	public InterfaceType interfaceType() {
		return interfaceType;
	}

	public Method method() {
		return method;
	}

	public Field field() {
		return field;
	}

	public ObjectReference objectReference() {
		return objectReference;
	}

	public StringReference stringReference() {
		return stringReference;
	}

	public ThreadReference threadReference() {
		return threadReference;
	}

	public ThreadGroupReference threadGroupReference() {
		return threadGroupReference;
	}

	public ArrayReference arrayReference() {
		return arrayReference;
	}

	public ClassLoaderReference classLoaderReference() {
		return classLoaderReference;
	}

	public EventRequest eventRequest() {
		return eventRequest;
	}

	public StackFrame stackFrame() {
		return stackFrame;
	}

	public ClassObjectReference classObjectReference() {
		return classObjectReference;
	}

	public Event event() {
		return event;
	}

	/**
	 * Returns the sizes of variably-sized data types in the target VM.The returned values indicate the
	 * number of bytes used by the identifiers in command and reply packets.
	 */
	public static class IDSizes {

		public static ByteBuffer encode() {
			ByteBuffer bytes = encodeCommandPacket(1, 7);
			setPacketLen(bytes);
			return bytes;
		}

		public static class IDSizesReplyData {
			/**
			 * fieldID size in bytes
			 */
			public int fieldIDSize;
			/**
			 * methodID size in bytes
			 */
			public int methodIDSize;
			/**
			 * objectID size in bytes
			 */
			public int objectIDSize;
			/**
			 * referenceTypeID size in bytes
			 */
			public int referenceTypeIDSize;
			/**
			 * frameID size in bytes
			 */
			public int frameIDSize;
		}

		public static IDSizesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
			IDSizesReplyData iDSizesReplyData = new IDSizesReplyData();
			iDSizesReplyData.fieldIDSize = JdwpInt.decode(bytes, start);
			start += JdwpInt.getSize();
			iDSizesReplyData.methodIDSize = JdwpInt.decode(bytes, start);
			start += JdwpInt.getSize();
			iDSizesReplyData.objectIDSize = JdwpInt.decode(bytes, start);
			start += JdwpInt.getSize();
			iDSizesReplyData.referenceTypeIDSize = JdwpInt.decode(bytes, start);
			start += JdwpInt.getSize();
			iDSizesReplyData.frameIDSize = JdwpInt.decode(bytes, start);
			start += JdwpInt.getSize();
			return iDSizesReplyData;
		}
	}

	/**
	 * Suspends the execution of the application running in the target VM. All Java threads currently
	 * running will be suspended.
	 */
	public static class Suspend {

		public static ByteBuffer encode() {
			ByteBuffer bytes = encodeCommandPacket(1, 8);
			setPacketLen(bytes);
			return bytes;
		}

		public static boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return bytes.length == PACKET_HEADER_SIZE;
		}
	}

	/**
	 * Resumes execution of the application after the suspend command or an event has stopped it.
	 * Suspensions of the Virtual Machine and individual threads are counted. If a particular thread is
	 * suspended n times, it must resumed n times before it will continue.
	 */
	public static class Resume {

		public static ByteBuffer encode() {
			ByteBuffer bytes = encodeCommandPacket(1, 9);
			setPacketLen(bytes);
			return bytes;
		}

		public static boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return bytes.length == PACKET_HEADER_SIZE;
		}
	}

	public static byte[] encodeHandShakePacket() {
		return HANDSHAKE_BYTES;
	}

	public static boolean decodeHandShakePacket(byte[] bytes) {
		return Arrays.equals(HANDSHAKE_BYTES, bytes);
	}

	public static int getPacketLength(byte[] bytes, int start) throws JdwpRuntimeException {
		return decodeInt(bytes, start);
	}

	public static int getPacketID(byte[] bytes, int start) throws JdwpRuntimeException {
		return decodeInt(bytes, start + 4);
	}

	public static byte getPacketFlags(byte[] bytes, int start) throws JdwpRuntimeException {
		return decodeByte(bytes, start + 8);
	}

	public static boolean isReplyPacket(byte[] bytes, int start) throws JdwpRuntimeException {
		return (decodeByte(bytes, start + 8) & 0xff) == 0x80;
	}

	public static boolean isEventPacket(byte[] bytes, int start) throws JdwpRuntimeException {
		return !isReplyPacket(bytes, start)
				&& getPacketCommandSetID(bytes, start) == 64
				&& getPacketCommandID(bytes, start) == 100;
	}

	public static short getPacketErrorCode(byte[] bytes, int start) throws JdwpRuntimeException {
		return decodeShort(bytes, start + 9);
	}

	public static int getPacketCommandSetID(byte[] bytes, int start) throws JdwpRuntimeException {
		return decodeByte(bytes, start + 9);
	}

	public static int getPacketCommandID(byte[] bytes, int start) throws JdwpRuntimeException {
		return decodeByte(bytes, start + 10);
	}

	public static void setPacketID(byte[] bytes, int id) {
		bytes[4] = (byte) (id >> 24);
		bytes[5] = (byte) (id >> 16);
		bytes[6] = (byte) (id >> 8);
		bytes[7] = (byte) (id);
	}

	private static void setPacketLen(ByteBuffer bytes) {
		int size = bytes.size();
		bytes.set(0, (byte) (size >> 24))
				.set(1, (byte) (size >> 16))
				.set(2, (byte) (size >> 8))
				.set(3, (byte) size);
	}

	private static ByteBuffer encodeCommandPacket(int setID, int commandID) {
		byte[] bytes = new byte[PACKET_HEADER_SIZE];
		bytes[PACKET_HEADER_SIZE - 2] = (byte) setID;
		bytes[PACKET_HEADER_SIZE - 1] = (byte) commandID;
		return new ByteBuffer(bytes);
	}

	private static void checkCapability(byte[] bytes, int start, int size) throws JdwpRuntimeException {
		if (bytes.length - start < size) {
			throw new JdwpRuntimeException(
					String.format("Insufficient space for decoding, size(%d) > bytes.length(%d).", size, bytes.length - start));
		}
	}

	public static long decodeBySize(byte[] bytes, int start, int size) throws JdwpRuntimeException {
		checkCapability(bytes, start, size);
		long rst = 0;
		int shift = 64 - 8;
		for (int i = 0; i < 8 - size; i++) {
			shift -= 8;
		}
		for (int i = 0; i < size && start < bytes.length; i++, start++) {
			rst += (long) (bytes[start] & 0xff) << (shift - i * 8);
		}
		return rst;
	}

	public static byte decodeByte(byte[] bytes, int start) throws JdwpRuntimeException {
		checkCapability(bytes, start, 1);
		return (byte) (bytes[start] & 0xff);
	}

	public static boolean decodeBoolean(byte[] bytes, int start) throws JdwpRuntimeException {
		checkCapability(bytes, start, 1);
		return (bytes[start] & 0xff) == 1;
	}

	public static short decodeShort(byte[] bytes, int start) throws JdwpRuntimeException {
		checkCapability(bytes, start, 2);
		short result;
		result = (short) ((bytes[start] & 0xff) << 8);
		result += (bytes[start + 1] & 0xff);
		return result;
	}

	public static char decodeChar(byte[] bytes, int start) throws JdwpRuntimeException {
		checkCapability(bytes, start, 2);
		char result;
		result = (char) ((bytes[start] & 0xff) << 8);
		result += (bytes[start + 1] & 0xff);
		return result;
	}

	public static int decodeInt(byte[] bytes, int start) throws JdwpRuntimeException {
		checkCapability(bytes, start, 4);
		int result = 0;
		result = (bytes[start] & 0xff) << 24;
		result += ((bytes[start + 1] & 0xff) << 16);
		result += ((bytes[start + 2] & 0xff) << 8);
		result += (bytes[start + 3] & 0xff);
		return result;
	}

	public static float decodeFloat(byte[] bytes, int start) throws JdwpRuntimeException {
		int val = decodeInt(bytes, start);
		return Float.intBitsToFloat(val);
	}

	public static double decodeDouble(byte[] bytes, int start) throws JdwpRuntimeException {
		long val = decodeBySize(bytes, start, 8);
		return Double.longBitsToDouble(val);
	}

	public static byte[] decodeRaw(byte[] bytes, int start, int size) throws JdwpRuntimeException {
		checkCapability(bytes, start, size);
		byte[] raw = new byte[size];
		System.arraycopy(bytes, start, raw, 0, size);
		return raw;
	}

	public static void encodeBySize(ByteBuffer bytes, int size, long val) {
		int shift = 64 - 8;
		for (int i = 0; i < 8 - size; i++) {
			shift -= 8;
		}
		for (int i = 0; i < size; i++) {
			bytes.add((byte) (val >> (shift - i * 8)));
		}
	}

	public static void encodeByte(ByteBuffer bytes, byte val) {
		bytes.add(val);
	}

	public static void encodeBoolean(ByteBuffer bytes, boolean val) {
		bytes.add((byte) (val ? 1 : 0));
	}

	public static void encodeShort(ByteBuffer bytes, short val) {
		bytes.add((byte) (val >> 8));
		bytes.add((byte) (val));
	}

	public static void encodeChar(ByteBuffer bytes, char val) {
		bytes.add((byte) (val >> 8));
		bytes.add((byte) (val));
	}

	public static void encodeInt(ByteBuffer bytes, int val) {
		bytes.add((byte) (val >> 24));
		bytes.add((byte) (val >> 16));
		bytes.add((byte) (val >> 8));
		bytes.add((byte) (val));
	}

	public static void encodeFloat(ByteBuffer bytes, float f) {
		int val = Float.floatToRawIntBits(f);
		bytes.add((byte) (val >> 24));
		bytes.add((byte) (val >> 16));
		bytes.add((byte) (val >> 8));
		bytes.add((byte) (val));
	}

	public static void encodeDouble(ByteBuffer bytes, double d) {
		long val = Double.doubleToRawLongBits(d);
		encodeBySize(bytes, 8, val);
	}

	public static void encodeString(ByteBuffer bytes, String str) {
		byte[] encoded = str.getBytes(StandardCharsets.UTF_8);
		encodeInt(bytes, encoded.length);
		encodeRaw(bytes, encoded);
	}

	public static void encodeRaw(ByteBuffer bytes, ByteBuffer raw) {
		bytes.addAll(raw);
	}

	public static void encodeRaw(ByteBuffer bytes, byte[] raw) {
		bytes.addAll(raw);
	}

	public static void encodeAny(ByteBuffer bytes, Object idOrValue) {
		if (idOrValue instanceof Integer) {
			JDWP.encodeInt(bytes, (int) idOrValue);
			return;
		}
		if (idOrValue instanceof String) {
			JDWP.encodeString(bytes, (String) idOrValue);
			return;
		}
		if (idOrValue instanceof Long) {
			encodeBySize(bytes, 8, (long) idOrValue);
			return;
		}
		if (idOrValue instanceof Float) {
			encodeFloat(bytes, (float) idOrValue);
			return;
		}
		if (idOrValue instanceof Double) {
			encodeDouble(bytes, (double) idOrValue);
			return;
		}
		if (idOrValue instanceof Byte) {
			JDWP.encodeByte(bytes, (byte) idOrValue);
			return;
		}
		if (idOrValue instanceof Character) {
			JDWP.encodeChar(bytes, (char) idOrValue);
			return;
		}
		if (idOrValue instanceof Short) {
			JDWP.encodeShort(bytes, (short) idOrValue);
			return;
		}
		if (idOrValue instanceof Boolean) {
			JDWP.encodeBoolean(bytes, (boolean) idOrValue);
			return;
		}
		throw new JdwpRuntimeException("Unexpected type: " + idOrValue);
	}

	public static class Packet {
		private byte[] buf;

		public static Packet make(byte[] bytes) {
			Packet header = new Packet();
			header.buf = bytes;
			return header;
		}

		public int getID() throws JdwpRuntimeException {
			return getPacketID(buf, 0);
		}

		public int getCommandSetID() throws JdwpRuntimeException {
			return getPacketCommandSetID(buf, 0);
		}

		public int getCommandID() throws JdwpRuntimeException {
			return getPacketCommandID(buf, 0);
		}

		public byte getFlags() throws JdwpRuntimeException {
			return getPacketFlags(buf, 0);
		}

		public short getErrorCode() throws JdwpRuntimeException {
			return getPacketErrorCode(buf, 0);
		}

		public boolean isReplyPacket() throws JdwpRuntimeException {
			return (getFlags() & 0xff) == 0x80;
		}

		public boolean isError() throws JdwpRuntimeException {
			return getErrorCode() != 0;
		}

		public String getErrorText() throws JdwpRuntimeException {
			return Error.getErrorText(getErrorCode());
		}

		public int getLength() {
			return buf.length;
		}

		public byte[] getBuf() {
			return buf;
		}
	}

	public class VirtualMachine {
		private final Version cmdVersion;
		private final ClassesBySignature cmdClassesBySignature;
		private final AllClasses cmdAllClasses;
		private final AllThreads cmdAllThreads;
		private final TopLevelThreadGroups cmdTopLevelThreadGroups;
		private final Dispose cmdDispose;
		private final Exit cmdExit;
		private final CreateString cmdCreateString;
		private final Capabilities cmdCapabilities;
		private final ClassPaths cmdClassPaths;
		private final DisposeObjects cmdDisposeObjects;
		private final HoldEvents cmdHoldEvents;
		private final ReleaseEvents cmdReleaseEvents;
		private final CapabilitiesNew cmdCapabilitiesNew;
		private final RedefineClasses cmdRedefineClasses;
		private final SetDefaultStratum cmdSetDefaultStratum;
		private final AllClassesWithGeneric cmdAllClassesWithGeneric;
		private final InstanceCounts cmdInstanceCounts;

		public Version cmdVersion() {
			return cmdVersion;
		}

		public ClassesBySignature cmdClassesBySignature() {
			return cmdClassesBySignature;
		}

		public AllClasses cmdAllClasses() {
			return cmdAllClasses;
		}

		public AllThreads cmdAllThreads() {
			return cmdAllThreads;
		}

		public TopLevelThreadGroups cmdTopLevelThreadGroups() {
			return cmdTopLevelThreadGroups;
		}

		public Dispose cmdDispose() {
			return cmdDispose;
		}

		public Exit cmdExit() {
			return cmdExit;
		}

		public CreateString cmdCreateString() {
			return cmdCreateString;
		}

		public Capabilities cmdCapabilities() {
			return cmdCapabilities;
		}

		public ClassPaths cmdClassPaths() {
			return cmdClassPaths;
		}

		public DisposeObjects cmdDisposeObjects() {
			return cmdDisposeObjects;
		}

		public HoldEvents cmdHoldEvents() {
			return cmdHoldEvents;
		}

		public ReleaseEvents cmdReleaseEvents() {
			return cmdReleaseEvents;
		}

		public CapabilitiesNew cmdCapabilitiesNew() {
			return cmdCapabilitiesNew;
		}

		public RedefineClasses cmdRedefineClasses() {
			return cmdRedefineClasses;
		}

		public SetDefaultStratum cmdSetDefaultStratum() {
			return cmdSetDefaultStratum;
		}

		public AllClassesWithGeneric cmdAllClassesWithGeneric() {
			return cmdAllClassesWithGeneric;
		}

		public InstanceCounts cmdInstanceCounts() {
			return cmdInstanceCounts;
		}

		private VirtualMachine() {
			cmdVersion = new Version();
			cmdClassesBySignature = new ClassesBySignature();
			cmdAllClasses = new AllClasses();
			cmdAllThreads = new AllThreads();
			cmdTopLevelThreadGroups = new TopLevelThreadGroups();
			cmdDispose = new Dispose();
			cmdExit = new Exit();
			cmdCreateString = new CreateString();
			cmdCapabilities = new Capabilities();
			cmdClassPaths = new ClassPaths();
			cmdDisposeObjects = new DisposeObjects();
			cmdHoldEvents = new HoldEvents();
			cmdReleaseEvents = new ReleaseEvents();
			cmdCapabilitiesNew = new CapabilitiesNew();
			cmdRedefineClasses = new RedefineClasses();
			cmdSetDefaultStratum = new SetDefaultStratum();
			cmdAllClassesWithGeneric = new AllClassesWithGeneric();
			cmdInstanceCounts = new InstanceCounts();
		}

		/**
		 * Returns the JDWP version implemented by the target VM. The version string format is
		 * implementation dependent.
		 */
		public class Version {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 1);
				setPacketLen(bytes);
				return bytes;
			}

			public class VersionReplyData {
				/**
				 * Text information on the VM version
				 */
				public String description;
				/**
				 * Major JDWP Version number
				 */
				public int jdwpMajor;
				/**
				 * Minor JDWP Version number
				 */
				public int jdwpMinor;
				/**
				 * Target VM JRE version, as in the java.version property
				 */
				public String vmVersion;
				/**
				 * Target VM name, as in the java.vm.name property
				 */
				public String vmName;
			}

			public VersionReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				VersionReplyData versionReplyData = new VersionReplyData();
				versionReplyData.description = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(versionReplyData.description);
				versionReplyData.jdwpMajor = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				versionReplyData.jdwpMinor = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				versionReplyData.vmVersion = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(versionReplyData.vmVersion);
				versionReplyData.vmName = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(versionReplyData.vmName);
				return versionReplyData;
			}
		}

		/**
		 * Returns reference types for all the classes loaded by the target VM which match the given
		 * signature. Multple reference types will be returned if two or more class loaders have loaded a
		 * class of the same name. The search is confined to loaded classes only; no attempt is made to load
		 * a class of the given signature.
		 */
		public class ClassesBySignature {

			/**
			 * @param signature JNI signature of the class to find (for example, "Ljava/lang/String;").
			 */
			public ByteBuffer encode(String signature) {
				ByteBuffer bytes = encodeCommandPacket(1, 2);
				JdwpString.encode(signature, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ClassesBySignatureReplyData {
				/**
				 * Number of reference types that follow.
				 */
				public List<ClassesBySignatureReplyDataClasses> classes;
			}

			public class ClassesBySignatureReplyDataClasses {
				/**
				 * Kind of following reference type.
				 */
				public byte refTypeTag;
				/**
				 * Matching loaded reference type
				 */
				public long typeID;
				/**
				 * The current class status.
				 */
				public int status;
			}

			public ClassesBySignatureReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ClassesBySignatureReplyData classesBySignatureReplyData = new ClassesBySignatureReplyData();
				int classesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				classesBySignatureReplyData.classes = new ArrayList<>(classesSize);
				for (int i = 0; i < classesSize; i++) {
					ClassesBySignatureReplyDataClasses classesBySignatureReplyDataClasses = new ClassesBySignatureReplyDataClasses();
					classesBySignatureReplyDataClasses.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					classesBySignatureReplyDataClasses.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					classesBySignatureReplyDataClasses.status = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					classesBySignatureReplyData.classes.add(classesBySignatureReplyDataClasses);
				}
				return classesBySignatureReplyData;
			}
		}

		/**
		 * Returns reference types for all classes currently loaded by the target VM.
		 */
		public class AllClasses {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 3);
				setPacketLen(bytes);
				return bytes;
			}

			public class AllClassesReplyData {
				/**
				 * Number of reference types that follow.
				 */
				public List<AllClassesReplyDataClasses> classes;
			}

			public class AllClassesReplyDataClasses {
				/**
				 * Kind of following reference type.
				 */
				public byte refTypeTag;
				/**
				 * Loaded reference type
				 */
				public long typeID;
				/**
				 * The JNI signature of the loaded reference type
				 */
				public String signature;
				/**
				 * The current class status.
				 */
				public int status;
			}

			public AllClassesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				AllClassesReplyData allClassesReplyData = new AllClassesReplyData();
				int classesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				allClassesReplyData.classes = new ArrayList<>(classesSize);
				for (int i = 0; i < classesSize; i++) {
					AllClassesReplyDataClasses allClassesReplyDataClasses = new AllClassesReplyDataClasses();
					allClassesReplyDataClasses.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					allClassesReplyDataClasses.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					allClassesReplyDataClasses.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(allClassesReplyDataClasses.signature);
					allClassesReplyDataClasses.status = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					allClassesReplyData.classes.add(allClassesReplyDataClasses);
				}
				return allClassesReplyData;
			}
		}

		/**
		 * Returns all threads currently running in the target VM . The returned list contains threads
		 * created through java.lang.Thread, all native threads attached to the target VM through JNI, and
		 * system threads created by the target VM. Threads that have not yet been started and threads that
		 * have completed their execution are not included in the returned list.
		 */
		public class AllThreads {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 4);
				setPacketLen(bytes);
				return bytes;
			}

			public class AllThreadsReplyData {
				/**
				 * Number of threads that follow.
				 */
				public List<AllThreadsReplyDataThreads> threads;
			}

			public class AllThreadsReplyDataThreads {
				/**
				 * A running thread
				 */
				public long thread;
			}

			public AllThreadsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				AllThreadsReplyData allThreadsReplyData = new AllThreadsReplyData();
				int threadsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				allThreadsReplyData.threads = new ArrayList<>(threadsSize);
				for (int i = 0; i < threadsSize; i++) {
					AllThreadsReplyDataThreads allThreadsReplyDataThreads = new AllThreadsReplyDataThreads();
					allThreadsReplyDataThreads.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					allThreadsReplyData.threads.add(allThreadsReplyDataThreads);
				}
				return allThreadsReplyData;
			}
		}

		/**
		 * Returns all thread groups that do not have a parent. This command may be used as the first step
		 * in building a tree (or trees) of the existing thread groups.
		 */
		public class TopLevelThreadGroups {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 5);
				setPacketLen(bytes);
				return bytes;
			}

			public class TopLevelThreadGroupsReplyData {
				/**
				 * Number of thread groups that follow.
				 */
				public List<TopLevelThreadGroupsReplyDataGroups> groups;
			}

			public class TopLevelThreadGroupsReplyDataGroups {
				/**
				 * A top level thread group
				 */
				public long group;
			}

			public TopLevelThreadGroupsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				TopLevelThreadGroupsReplyData topLevelThreadGroupsReplyData = new TopLevelThreadGroupsReplyData();
				int groupsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				topLevelThreadGroupsReplyData.groups = new ArrayList<>(groupsSize);
				for (int i = 0; i < groupsSize; i++) {
					TopLevelThreadGroupsReplyDataGroups topLevelThreadGroupsReplyDataGroups = new TopLevelThreadGroupsReplyDataGroups();
					topLevelThreadGroupsReplyDataGroups.group = mThreadGroupID.decode(bytes, start);
					start += mThreadGroupID.getSize();
					topLevelThreadGroupsReplyData.groups.add(topLevelThreadGroupsReplyDataGroups);
				}
				return topLevelThreadGroupsReplyData;
			}
		}

		/**
		 * Invalidates this virtual machine mirror. The communication channel to the target VM is closed,
		 * and the target VM prepares to accept another subsequent connection from this debugger or another
		 * debugger, including the following tasks:
		 */
		public class Dispose {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 6);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Terminates the target VM with the given exit code. On some platforms, the exit code might be
		 * truncated, for example, to the low order 8 bits. All ids previously returned from the target VM
		 * become invalid. Threads running in the VM are abruptly terminated. A thread death exception is
		 * not thrown and finally blocks are not run.
		 */
		public class Exit {

			/**
			 * @param exitCode the exit code
			 */
			public ByteBuffer encode(int exitCode) {
				ByteBuffer bytes = encodeCommandPacket(1, 10);
				JdwpInt.encode(exitCode, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Creates a new string object in the target VM and returns its id.
		 */
		public class CreateString {

			/**
			 * @param utf UTF-8 characters to use in the created string.
			 */
			public ByteBuffer encode(String utf) {
				ByteBuffer bytes = encodeCommandPacket(1, 11);
				JdwpString.encode(utf, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class CreateStringReplyData {
				/**
				 * Created string (instance of java.lang.String)
				 */
				public long stringObject;
			}

			public CreateStringReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				CreateStringReplyData createStringReplyData = new CreateStringReplyData();
				createStringReplyData.stringObject = mStringID.decode(bytes, start);
				start += mStringID.getSize();
				return createStringReplyData;
			}
		}

		/**
		 * Retrieve this VM's capabilities. The capabilities are returned as booleans, each indicating the
		 * presence or absence of a capability. The commands associated with each capability will return the
		 * NOT_IMPLEMENTED error if the cabability is not available.
		 */
		public class Capabilities {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 12);
				setPacketLen(bytes);
				return bytes;
			}

			public class CapabilitiesReplyData {
				/**
				 * Can the VM watch field modification, and therefore can it send the Modification Watchpoint Event?
				 */
				public boolean canWatchFieldModification;
				/**
				 * Can the VM watch field access, and therefore can it send the Access Watchpoint Event?
				 */
				public boolean canWatchFieldAccess;
				/**
				 * Can the VM get the bytecodes of a given method?
				 */
				public boolean canGetBytecodes;
				/**
				 * Can the VM determine whether a field or method is synthetic? (that is, can the VM determine if
				 * the method or the field was invented by the compiler?)
				 */
				public boolean canGetSyntheticAttribute;
				/**
				 * Can the VM get the owned monitors infornation for a thread?
				 */
				public boolean canGetOwnedMonitorInfo;
				/**
				 * Can the VM get the current contended monitor of a thread?
				 */
				public boolean canGetCurrentContendedMonitor;
				/**
				 * Can the VM get the monitor information for a given object?
				 */
				public boolean canGetMonitorInfo;
			}

			public CapabilitiesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				CapabilitiesReplyData capabilitiesReplyData = new CapabilitiesReplyData();
				capabilitiesReplyData.canWatchFieldModification = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesReplyData.canWatchFieldAccess = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesReplyData.canGetBytecodes = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesReplyData.canGetSyntheticAttribute = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesReplyData.canGetOwnedMonitorInfo = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesReplyData.canGetCurrentContendedMonitor = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesReplyData.canGetMonitorInfo = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				return capabilitiesReplyData;
			}
		}

		/**
		 * Retrieve the classpath and bootclasspath of the target VM. If the classpath is not defined,
		 * returns an empty list. If the bootclasspath is not defined returns an empty list.
		 */
		public class ClassPaths {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 13);
				setPacketLen(bytes);
				return bytes;
			}

			public class ClassPathsReplyData {
				/**
				 * Base directory used to resolve relative paths in either of the following lists.
				 */
				public String baseDir;
				/**
				 * Number of paths in classpath.
				 */
				public List<ClassPathsReplyDataClasspaths> classpaths;
			}

			public class ClassPathsReplyDataClasspaths {
				/**
				 * One component of classpath
				 */
				public String path;
				/**
				 * Number of paths in bootclasspath.
				 */
				public List<ClassPathsReplyDataBootclasspaths> bootclasspaths;
			}

			public class ClassPathsReplyDataBootclasspaths {
				/**
				 * One component of bootclasspath
				 */
				public String path;
			}

			public ClassPathsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ClassPathsReplyData classPathsReplyData = new ClassPathsReplyData();
				classPathsReplyData.baseDir = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(classPathsReplyData.baseDir);

				int classpathsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				classPathsReplyData.classpaths = new ArrayList<>(classpathsSize);
				for (int i = 0; i < classpathsSize; i++) {
					ClassPathsReplyDataClasspaths classPathsReplyDataClasspaths = new ClassPathsReplyDataClasspaths();
					classPathsReplyDataClasspaths.path = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(classPathsReplyDataClasspaths.path);

					int bootclasspathsSize = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					classPathsReplyDataClasspaths.bootclasspaths = new ArrayList<>(bootclasspathsSize);
					for (int ii = 0; ii < bootclasspathsSize; ii++) {
						ClassPathsReplyDataBootclasspaths classPathsReplyDataBootclasspaths = new ClassPathsReplyDataBootclasspaths();
						classPathsReplyDataBootclasspaths.path = JdwpString.decode(bytes, start);
						start += JdwpString.getSize(classPathsReplyDataBootclasspaths.path);
						classPathsReplyDataClasspaths.bootclasspaths.add(classPathsReplyDataBootclasspaths);
					}
					classPathsReplyData.classpaths.add(classPathsReplyDataClasspaths);
				}
				return classPathsReplyData;
			}
		}

		/**
		 * Releases a list of object IDs. For each object in the list, the following applies. The count of
		 * references held by the back-end (the reference count) will be decremented by refCnt. If
		 * thereafter the reference count is less than or equal to zero, the ID is freed. Any back-end
		 * resources associated with the freed ID may be freed, and if garbage collection was disabled for
		 * the object, it will be re-enabled. The sender of this command promises that no further commands
		 * will be sent referencing a freed ID.
		 */
		public class DisposeObjects {

			public class DisposeObjectsRequests {
				/**
				 * The object ID
				 */
				public long object;
				/**
				 * The number of times this object ID has been part of a packet received from the back-end. An
				 * accurate count prevents the object ID from being freed on the back-end if it is part of an
				 * incoming packet, not yet handled by the front-end.
				 */
				public int refCnt;
			}

			/**
			 * @param requests Number of object dispose requests that follow
			 */
			public ByteBuffer encode(List<DisposeObjectsRequests> requests) {
				ByteBuffer bytes = encodeCommandPacket(1, 14);
				JdwpInt.encode(requests.size(), bytes);
				for (DisposeObjectsRequests disposeObjectsRequests : requests) {
					mObjectID.encode(disposeObjectsRequests.object, bytes);
					JdwpInt.encode(disposeObjectsRequests.refCnt, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Tells the target VM to stop sending events. Events are not discarded; they are held until a
		 * subsequent ReleaseEvents command is sent. This command is useful to control the number of events
		 * sent to the debugger VM in situations where very large numbers of events are generated. While
		 * events are held by the debugger back-end, application execution may be frozen by the debugger
		 * back-end to prevent buffer overflows on the back end. Responses to commands are never held and
		 * are not affected by this command. If events are already being held, this command is ignored.
		 */
		public class HoldEvents {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 15);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Tells the target VM to continue sending events. This command is used to restore normal activity
		 * after a HoldEvents command. If there is no current HoldEvents command in effect, this command is
		 * ignored.
		 */
		public class ReleaseEvents {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 16);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Retrieve all of this VM's capabilities. The capabilities are returned as booleans, each
		 * indicating the presence or absence of a capability. The commands associated with each capability
		 * will return the NOT_IMPLEMENTED error if the cabability is not available.Since JDWP version 1.4.
		 */
		public class CapabilitiesNew {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 17);
				setPacketLen(bytes);
				return bytes;
			}

			public class CapabilitiesNewReplyData {
				/**
				 * Can the VM watch field modification, and therefore can it send the Modification Watchpoint Event?
				 */
				public boolean canWatchFieldModification;
				/**
				 * Can the VM watch field access, and therefore can it send the Access Watchpoint Event?
				 */
				public boolean canWatchFieldAccess;
				/**
				 * Can the VM get the bytecodes of a given method?
				 */
				public boolean canGetBytecodes;
				/**
				 * Can the VM determine whether a field or method is synthetic? (that is, can the VM determine if
				 * the method or the field was invented by the compiler?)
				 */
				public boolean canGetSyntheticAttribute;
				/**
				 * Can the VM get the owned monitors infornation for a thread?
				 */
				public boolean canGetOwnedMonitorInfo;
				/**
				 * Can the VM get the current contended monitor of a thread?
				 */
				public boolean canGetCurrentContendedMonitor;
				/**
				 * Can the VM get the monitor information for a given object?
				 */
				public boolean canGetMonitorInfo;
				/**
				 * Can the VM redefine classes?
				 */
				public boolean canRedefineClasses;
				/**
				 * Can the VM add methods when redefining classes?
				 */
				public boolean canAddMethod;
				/**
				 * Can the VM redefine classesin arbitrary ways?
				 */
				public boolean canUnrestrictedlyRedefineClasses;
				/**
				 * Can the VM pop stack frames?
				 */
				public boolean canPopFrames;
				/**
				 * Can the VM filter events by specific object?
				 */
				public boolean canUseInstanceFilters;
				/**
				 * Can the VM get the source debug extension?
				 */
				public boolean canGetSourceDebugExtension;
				/**
				 * Can the VM request VM death events?
				 */
				public boolean canRequestVMDeathEvent;
				/**
				 * Can the VM set a default stratum?
				 */
				public boolean canSetDefaultStratum;
				/**
				 * Can the VM return instances, counts of instances of classes and referring objects?
				 */
				public boolean canGetInstanceInfo;
				/**
				 * Can the VM request monitor events?
				 */
				public boolean canRequestMonitorEvents;
				/**
				 * Can the VM get monitors with frame depth info?
				 */
				public boolean canGetMonitorFrameInfo;
				/**
				 * Can the VM filter class prepare events by source name?
				 */
				public boolean canUseSourceNameFilters;
				/**
				 * Can the VM return the constant pool information?
				 */
				public boolean canGetConstantPool;
				/**
				 * Can the VM force early return from a method?
				 */
				public boolean canForceEarlyReturn;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved22;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved23;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved24;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved25;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved26;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved27;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved28;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved29;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved30;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved31;
				/**
				 * Reserved for future capability
				 */
				public boolean reserved32;
			}

			public CapabilitiesNewReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				CapabilitiesNewReplyData capabilitiesNewReplyData = new CapabilitiesNewReplyData();
				capabilitiesNewReplyData.canWatchFieldModification = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canWatchFieldAccess = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetBytecodes = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetSyntheticAttribute = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetOwnedMonitorInfo = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetCurrentContendedMonitor = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetMonitorInfo = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canRedefineClasses = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canAddMethod = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canUnrestrictedlyRedefineClasses = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canPopFrames = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canUseInstanceFilters = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetSourceDebugExtension = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canRequestVMDeathEvent = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canSetDefaultStratum = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetInstanceInfo = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canRequestMonitorEvents = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetMonitorFrameInfo = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canUseSourceNameFilters = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canGetConstantPool = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.canForceEarlyReturn = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved22 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved23 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved24 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved25 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved26 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved27 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved28 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved29 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved30 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved31 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				capabilitiesNewReplyData.reserved32 = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				return capabilitiesNewReplyData;
			}
		}

		/**
		 * Installs new class definitions. If there are active stack frames in methods of the redefined
		 * classes in the target VM then those active frames continue to run the bytecodes of the original
		 * method. These methods are considered obsolete - see IsObsolete. The methods in the redefined
		 * classes will be used for new invokes in the target VM. The original method ID refers to the
		 * redefined method. All breakpoints in the redefined classes are cleared.If resetting of stack
		 * frames is desired, the PopFrames command can be used to pop frames with obsolete methods.
		 */
		public class RedefineClasses {

			public class RedefineClassesClasses {
				/**
				 * The reference type.
				 */
				public long refType;
				/**
				 * Number of bytes defining class (below)
				 */
				public List<RedefineClassesClassfile> classfile;
			}

			public class RedefineClassesClassfile {
				/**
				 * byte in JVM class file format.
				 */
				public byte classbyte;
			}

			/**
			 * @param classes Number of reference types that follow.
			 */
			public ByteBuffer encode(List<RedefineClassesClasses> classes) {
				ByteBuffer bytes = encodeCommandPacket(1, 18);
				JdwpInt.encode(classes.size(), bytes);
				for (RedefineClassesClasses redefineClassesClasses : classes) {
					mReferenceTypeID.encode(redefineClassesClasses.refType, bytes);
					for (int ii = 0; ii < redefineClassesClasses.classfile.size(); ii++) {
						RedefineClassesClassfile redefineClassesClassfile = redefineClassesClasses.classfile.get(ii);
						JdwpByte.encode(redefineClassesClassfile.classbyte, bytes);
					}
				}
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Set the default stratum. Requires canSetDefaultStratum capability - see CapabilitiesNew.
		 */
		public class SetDefaultStratum {

			/**
			 * @param stratumID default stratum, or empty string to use reference type default.
			 */
			public ByteBuffer encode(String stratumID) {
				ByteBuffer bytes = encodeCommandPacket(1, 19);
				JdwpString.encode(stratumID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Returns reference types for all classes currently loaded by the target VM. Both the JNI signature
		 * and the generic signature are returned for each class. Generic signatures are described in the
		 * signature attribute section in The Java™ Virtual Machine Specification. Since JDWP version 1.5.
		 */
		public class AllClassesWithGeneric {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(1, 20);
				setPacketLen(bytes);
				return bytes;
			}

			public class AllClassesWithGenericReplyData {
				/**
				 * Number of reference types that follow.
				 */
				public List<AllClassesWithGenericData> classes;
			}

			public class AllClassesWithGenericData {
				/**
				 * Kind of following reference type.
				 */
				public byte refTypeTag;
				/**
				 * Loaded reference type
				 */
				public long typeID;
				/**
				 * The JNI signature of the loaded reference type.
				 */
				public String signature;
				/**
				 * The generic signature of the loaded reference type or an empty string if there is none.
				 */
				public String genericSignature;
				/**
				 * The current class status.
				 */
				public int status;
			}

			public AllClassesWithGenericReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				AllClassesWithGenericReplyData allClassesWithGenericReplyData = new AllClassesWithGenericReplyData();
				int classesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				allClassesWithGenericReplyData.classes = new ArrayList<>(classesSize);
				for (int i = 0; i < classesSize; i++) {
					AllClassesWithGenericData allClassesWithGenericReplyDataClasses =
							new AllClassesWithGenericData();
					allClassesWithGenericReplyDataClasses.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					allClassesWithGenericReplyDataClasses.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					allClassesWithGenericReplyDataClasses.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(allClassesWithGenericReplyDataClasses.signature);
					allClassesWithGenericReplyDataClasses.genericSignature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(allClassesWithGenericReplyDataClasses.genericSignature);
					allClassesWithGenericReplyDataClasses.status = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					allClassesWithGenericReplyData.classes.add(allClassesWithGenericReplyDataClasses);
				}
				return allClassesWithGenericReplyData;
			}
		}

		/**
		 * Returns the number of instances of each reference type in the input list. Only instances that are
		 * reachable for the purposes of garbage collection are counted. If a reference type is invalid, eg.
		 * it has been unloaded, zero is returned for its instance count.
		 */
		public class InstanceCounts {

			public class InstanceCountsRefTypesCount {
				/**
				 * A reference type ID.
				 */
				public long refType;
			}

			/**
			 * @param refTypesCount Number of reference types that follow. Must be non-negative.
			 */
			public ByteBuffer encode(List<InstanceCountsRefTypesCount> refTypesCount) {
				ByteBuffer bytes = encodeCommandPacket(1, 21);
				JdwpInt.encode(refTypesCount.size(), bytes);
				for (InstanceCountsRefTypesCount instanceCountsRefTypesCount : refTypesCount) {
					mReferenceTypeID.encode(instanceCountsRefTypesCount.refType, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public class InstanceCountsReplyData {
				/**
				 * The number of counts that follow.
				 */
				public List<InstanceCountsReplyDataCounts> counts;
			}

			public class InstanceCountsReplyDataCounts {
				/**
				 * The number of instances for the corresponding reference type in 'Out Data'.
				 */
				public long instanceCount;
			}

			public InstanceCountsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				InstanceCountsReplyData instanceCountsReplyData = new InstanceCountsReplyData();
				int countsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				instanceCountsReplyData.counts = new ArrayList<>(countsSize);
				for (int i = 0; i < countsSize; i++) {
					InstanceCountsReplyDataCounts instanceCountsReplyDataCounts = new InstanceCountsReplyDataCounts();
					instanceCountsReplyDataCounts.instanceCount = JdwpLong.decode(bytes, start);
					start += JdwpLong.getSize();
					instanceCountsReplyData.counts.add(instanceCountsReplyDataCounts);
				}
				return instanceCountsReplyData;
			}
		}
	}

	public class ReferenceType {
		private final Signature cmdSignature;
		private final ClassLoader cmdClassLoader;
		private final Modifiers cmdModifiers;
		private final Fields cmdFields;
		private final Methods cmdMethods;
		private final GetValues cmdGetValues;
		private final SourceFile cmdSourceFile;
		private final NestedTypes cmdNestedTypes;
		private final Status cmdStatus;
		private final Interfaces cmdInterfaces;
		private final ClassObject cmdClassObject;
		private final SourceDebugExtension cmdSourceDebugExtension;
		private final SignatureWithGeneric cmdSignatureWithGeneric;
		private final FieldsWithGeneric cmdFieldsWithGeneric;
		private final MethodsWithGeneric cmdMethodsWithGeneric;
		private final Instances cmdInstances;
		private final ClassFileVersion cmdClassFileVersion;
		private final ConstantPool cmdConstantPool;

		public Signature cmdSignature() {
			return cmdSignature;
		}

		public ClassLoader cmdClassLoader() {
			return cmdClassLoader;
		}

		public Modifiers cmdModifiers() {
			return cmdModifiers;
		}

		public Fields cmdFields() {
			return cmdFields;
		}

		public Methods cmdMethods() {
			return cmdMethods;
		}

		public GetValues cmdGetValues() {
			return cmdGetValues;
		}

		public SourceFile cmdSourceFile() {
			return cmdSourceFile;
		}

		public NestedTypes cmdNestedTypes() {
			return cmdNestedTypes;
		}

		public Status cmdStatus() {
			return cmdStatus;
		}

		public Interfaces cmdInterfaces() {
			return cmdInterfaces;
		}

		public ClassObject cmdClassObject() {
			return cmdClassObject;
		}

		public SourceDebugExtension cmdSourceDebugExtension() {
			return cmdSourceDebugExtension;
		}

		public SignatureWithGeneric cmdSignatureWithGeneric() {
			return cmdSignatureWithGeneric;
		}

		public FieldsWithGeneric cmdFieldsWithGeneric() {
			return cmdFieldsWithGeneric;
		}

		public MethodsWithGeneric cmdMethodsWithGeneric() {
			return cmdMethodsWithGeneric;
		}

		public Instances cmdInstances() {
			return cmdInstances;
		}

		public ClassFileVersion cmdClassFileVersion() {
			return cmdClassFileVersion;
		}

		public ConstantPool cmdConstantPool() {
			return cmdConstantPool;
		}

		private ReferenceType() {
			cmdSignature = new Signature();
			cmdClassLoader = new ClassLoader();
			cmdModifiers = new Modifiers();
			cmdFields = new Fields();
			cmdMethods = new Methods();
			cmdGetValues = new GetValues();
			cmdSourceFile = new SourceFile();
			cmdNestedTypes = new NestedTypes();
			cmdStatus = new Status();
			cmdInterfaces = new Interfaces();
			cmdClassObject = new ClassObject();
			cmdSourceDebugExtension = new SourceDebugExtension();
			cmdSignatureWithGeneric = new SignatureWithGeneric();
			cmdFieldsWithGeneric = new FieldsWithGeneric();
			cmdMethodsWithGeneric = new MethodsWithGeneric();
			cmdInstances = new Instances();
			cmdClassFileVersion = new ClassFileVersion();
			cmdConstantPool = new ConstantPool();
		}

		/**
		 * Returns the JNI signature of a reference type. JNI signature formats are described in the Java
		 * Native Inteface Specification
		 */
		public class Signature {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 1);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class SignatureReplyData {
				/**
				 * The JNI signature for the reference type.
				 */
				public String signature;
			}

			public SignatureReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				SignatureReplyData signatureReplyData = new SignatureReplyData();
				signatureReplyData.signature = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(signatureReplyData.signature);
				return signatureReplyData;
			}
		}

		/**
		 * Returns the instance of java.lang.ClassLoader which loaded a given reference type. If the
		 * reference type was loaded by the system class loader, the returned object ID is null.
		 */
		public class ClassLoader {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 2);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ClassLoaderReplyData {
				/**
				 * The class loader for the reference type.
				 */
				public long classLoader;
			}

			public ClassLoaderReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ClassLoaderReplyData classLoaderReplyData = new ClassLoaderReplyData();
				classLoaderReplyData.classLoader = mClassLoaderID.decode(bytes, start);
				start += mClassLoaderID.getSize();
				return classLoaderReplyData;
			}
		}

		/**
		 * Returns the modifiers (also known as access flags) for a reference type. The returned bit mask
		 * contains information on the declaration of the reference type. If the reference type is an array
		 * or a primitive class (for example, java.lang.Integer.TYPE), the value of the returned bit mask is
		 * undefined.
		 */
		public class Modifiers {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 3);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ModifiersReplyData {
				/**
				 * Modifier bits as defined in Chapter 4 of The Java™ Virtual Machine Specification
				 */
				public int modBits;
			}

			public ModifiersReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ModifiersReplyData modifiersReplyData = new ModifiersReplyData();
				modifiersReplyData.modBits = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				return modifiersReplyData;
			}
		}

		/**
		 * Returns information for each field in a reference type. Inherited fields are not included. The
		 * field list will include any synthetic fields created by the compiler. Fields are returned in the
		 * order they occur in the class file.
		 */
		public class Fields {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 4);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class FieldsReplyData {
				/**
				 * Number of declared fields.
				 */
				public List<FieldsReplyDataDeclared> declared;
			}

			public class FieldsReplyDataDeclared {
				/**
				 * Field ID.
				 */
				public long fieldID;
				/**
				 * Name of field.
				 */
				public String name;
				/**
				 * JNI Signature of field.
				 */
				public String signature;
				/**
				 * The modifier bit flags (also known as access flags) which provide additional information on the
				 * field declaration. Individual flag values are defined in Chapter 4 of The Java™ Virtual Machine
				 * Specification. In addition, The 0xf0000000 bit identifies the field as synthetic, if the
				 * synthetic attribute capability is available.
				 */
				public int modBits;
			}

			public FieldsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				FieldsReplyData fieldsReplyData = new FieldsReplyData();
				int declaredSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				fieldsReplyData.declared = new ArrayList<>(declaredSize);
				for (int i = 0; i < declaredSize; i++) {
					FieldsReplyDataDeclared fieldsReplyDataDeclared = new FieldsReplyDataDeclared();
					fieldsReplyDataDeclared.fieldID = mFieldID.decode(bytes, start);
					start += mFieldID.getSize();
					fieldsReplyDataDeclared.name = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(fieldsReplyDataDeclared.name);
					fieldsReplyDataDeclared.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(fieldsReplyDataDeclared.signature);
					fieldsReplyDataDeclared.modBits = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					fieldsReplyData.declared.add(fieldsReplyDataDeclared);
				}
				return fieldsReplyData;
			}
		}

		/**
		 * Returns information for each method in a reference type. Inherited methods are not included. The
		 * list of methods will include constructors (identified with the name {@code "<init>"}), the initialization
		 * method (identified with the name {@code "<clinit>"}) if present, and any synthetic methods created by the
		 * compiler. Methods are returned in the order they occur in the class file.
		 */
		public class Methods {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 5);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class MethodsReplyData {
				/**
				 * Number of declared methods.
				 */
				public List<MethodsReplyDataDeclared> declared;
			}

			public class MethodsReplyDataDeclared {
				/**
				 * Method ID.
				 */
				public long methodID;
				/**
				 * Name of method.
				 */
				public String name;
				/**
				 * JNI signature of method.
				 */
				public String signature;
				/**
				 * The modifier bit flags (also known as access flags) which provide additional information on the
				 * method declaration. Individual flag values are defined in Chapter 4 of The Java™ Virtual Machine
				 * Specification. In addition, The 0xf0000000 bit identifies the method as synthetic, if the
				 * synthetic attribute capability is available.
				 */
				public int modBits;
			}

			public MethodsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				MethodsReplyData methodsReplyData = new MethodsReplyData();
				int declaredSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				methodsReplyData.declared = new ArrayList<>(declaredSize);
				for (int i = 0; i < declaredSize; i++) {
					MethodsReplyDataDeclared methodsReplyDataDeclared = new MethodsReplyDataDeclared();
					methodsReplyDataDeclared.methodID = mMethodID.decode(bytes, start);
					start += mMethodID.getSize();
					methodsReplyDataDeclared.name = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(methodsReplyDataDeclared.name);
					methodsReplyDataDeclared.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(methodsReplyDataDeclared.signature);
					methodsReplyDataDeclared.modBits = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					methodsReplyData.declared.add(methodsReplyDataDeclared);
				}
				return methodsReplyData;
			}
		}

		/**
		 * Returns the value of one or more static fields of the reference type. Each field must be member
		 * of the reference type or one of its superclasses, superinterfaces, or implemented interfaces.
		 * Access control is not enforced; for example, the values of private fields can be obtained.
		 */
		public class GetValues {

			/**
			 * @param refType The reference type ID.
			 * @param fields  The number of values to get
			 */
			public ByteBuffer encode(long refType, List<Long> fields) {
				ByteBuffer bytes = encodeCommandPacket(2, 6);
				mReferenceTypeID.encode(refType, bytes);
				JdwpInt.encode(fields.size(), bytes);
				for (Long aLong : fields) {
					mFieldID.encode(aLong, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public class GetValuesReplyData {
				/**
				 * The number of values returned, always equal to fields, the number of values to get.
				 */
				public List<GetValuesReplyDataValues> values;
			}

			public class GetValuesReplyDataValues {
				/**
				 * The field value
				 */
				public ValuePacket value;
			}

			public GetValuesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				GetValuesReplyData getValuesReplyData = new GetValuesReplyData();
				int valuesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				getValuesReplyData.values = new ArrayList<>(valuesSize);
				for (int i = 0; i < valuesSize; i++) {
					GetValuesReplyDataValues getValuesReplyDataValues = new GetValuesReplyDataValues();
					getValuesReplyDataValues.value = mValue.decode(bytes, start);
					start += mValue.getSize(getValuesReplyDataValues.value.tag);
					getValuesReplyData.values.add(getValuesReplyDataValues);
				}
				return getValuesReplyData;
			}
		}

		/**
		 * Returns the name of source file in which a reference type was declared.
		 */
		public class SourceFile {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 7);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class SourceFileReplyData {
				/**
				 * The source file name. No path information for the file is included
				 */
				public String sourceFile;
			}

			public SourceFileReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				SourceFileReplyData sourceFileReplyData = new SourceFileReplyData();
				sourceFileReplyData.sourceFile = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(sourceFileReplyData.sourceFile);
				return sourceFileReplyData;
			}
		}

		/**
		 * Returns the classes and interfaces directly nested within this type.Types further nested within
		 * those types are not included.
		 */
		public class NestedTypes {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 8);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class NestedTypesReplyData {
				/**
				 * The number of nested classes and interfaces
				 */
				public List<NestedTypesReplyDataClasses> classes;
			}

			public class NestedTypesReplyDataClasses {
				/**
				 * Kind of following reference type.
				 */
				public byte refTypeTag;
				/**
				 * The nested class or interface ID.
				 */
				public long typeID;
			}

			public NestedTypesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				NestedTypesReplyData nestedTypesReplyData = new NestedTypesReplyData();
				int classesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				nestedTypesReplyData.classes = new ArrayList<>(classesSize);
				for (int i = 0; i < classesSize; i++) {
					NestedTypesReplyDataClasses nestedTypesReplyDataClasses = new NestedTypesReplyDataClasses();
					nestedTypesReplyDataClasses.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					nestedTypesReplyDataClasses.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					nestedTypesReplyData.classes.add(nestedTypesReplyDataClasses);
				}
				return nestedTypesReplyData;
			}
		}

		/**
		 * Returns the current status of the reference type. The status indicates the extent to which the
		 * reference type has been initialized, as described in section 2.1.6 of The Java™ Virtual Machine
		 * Specification. If the class is linked the PREPARED and VERIFIED bits in the returned status bits
		 * will be set. If the class is initialized the INITIALIZED bit in the returned status bits will be
		 * set. If an error occured during initialization then the ERROR bit in the returned status bits
		 * will be set. The returned status bits are undefined for array types and for primitive classes
		 * (such as java.lang.Integer.TYPE).
		 */
		public class Status {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 9);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class StatusReplyData {
				/**
				 * Status bits:See JDWP.ClassStatus
				 */
				public int status;
			}

			public StatusReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				StatusReplyData statusReplyData = new StatusReplyData();
				statusReplyData.status = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				return statusReplyData;
			}
		}

		/**
		 * Returns the interfaces declared as implemented by this class. Interfaces indirectly implemented
		 * (extended by the implemented interface or implemented by a superclass) are not included.
		 */
		public class Interfaces {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 10);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class InterfacesReplyData {
				/**
				 * The number of implemented interfaces
				 */
				public List<InterfacesReplyDataInterfaces> interfaces;
			}

			public class InterfacesReplyDataInterfaces {
				/**
				 * implemented interface.
				 */
				public long interfaceType;
			}

			public InterfacesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				InterfacesReplyData interfacesReplyData = new InterfacesReplyData();
				int interfacesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				interfacesReplyData.interfaces = new ArrayList<>(interfacesSize);
				for (int i = 0; i < interfacesSize; i++) {
					InterfacesReplyDataInterfaces interfacesReplyDataInterfaces = new InterfacesReplyDataInterfaces();
					interfacesReplyDataInterfaces.interfaceType = mInterfaceID.decode(bytes, start);
					start += mInterfaceID.getSize();
					interfacesReplyData.interfaces.add(interfacesReplyDataInterfaces);
				}
				return interfacesReplyData;
			}
		}

		/**
		 * Returns the class object corresponding to this type.
		 */
		public class ClassObject {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 11);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ClassObjectReplyData {
				/**
				 * class object.
				 */
				public long classObject;
			}

			public ClassObjectReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ClassObjectReplyData classObjectReplyData = new ClassObjectReplyData();
				classObjectReplyData.classObject = mClassObjectID.decode(bytes, start);
				start += mClassObjectID.getSize();
				return classObjectReplyData;
			}
		}

		/**
		 * Returns the value of the SourceDebugExtension attribute. Since JDWP version 1.4. Requires
		 * canGetSourceDebugExtension capability - see CapabilitiesNew.
		 */
		public class SourceDebugExtension {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 12);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class SourceDebugExtensionReplyData {
				/**
				 * extension attribute
				 */
				public String extension;
			}

			public SourceDebugExtensionReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				SourceDebugExtensionReplyData sourceDebugExtensionReplyData = new SourceDebugExtensionReplyData();
				sourceDebugExtensionReplyData.extension = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(sourceDebugExtensionReplyData.extension);
				return sourceDebugExtensionReplyData;
			}
		}

		/**
		 * Returns the JNI signature of a reference type along with the generic signature if there is one.
		 * Generic signatures are described in the signature attribute section in The Java™ Virtual Machine
		 * Specification. Since JDWP version 1.5.
		 */
		public class SignatureWithGeneric {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 13);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class SignatureWithGenericReplyData {
				/**
				 * The JNI signature for the reference type.
				 */
				public String signature;
				/**
				 * The generic signature for the reference type or an empty string if there is none.
				 */
				public String genericSignature;
			}

			public SignatureWithGenericReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				SignatureWithGenericReplyData signatureWithGenericReplyData = new SignatureWithGenericReplyData();
				signatureWithGenericReplyData.signature = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(signatureWithGenericReplyData.signature);
				signatureWithGenericReplyData.genericSignature = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(signatureWithGenericReplyData.genericSignature);
				return signatureWithGenericReplyData;
			}
		}

		/**
		 * Returns information, including the generic signature if any, for each field in a reference type.
		 * Inherited fields are not included. The field list will include any synthetic fields created by
		 * the compiler. Fields are returned in the order they occur in the class file. Generic signatures
		 * are described in the signature attribute section in The Java™ Virtual Machine Specification.
		 * Since JDWP version 1.5.
		 */
		public class FieldsWithGeneric {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 14);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class FieldsWithGenericReplyData {
				/**
				 * Number of declared fields.
				 */
				public List<FieldsWithGenericData> declared;
			}

			public class FieldsWithGenericData {
				/**
				 * Field ID.
				 */
				public long fieldID;
				/**
				 * The name of the field.
				 */
				public String name;
				/**
				 * The JNI signature of the field.
				 */
				public String signature;
				/**
				 * The generic signature of the field, or an empty string if there is none.
				 */
				public String genericSignature;
				/**
				 * The modifier bit flags (also known as access flags) which provide additional information on the
				 * field declaration. Individual flag values are defined in Chapter 4 of The Java™ Virtual Machine
				 * Specification. In addition, The 0xf0000000 bit identifies the field as synthetic, if the
				 * synthetic attribute capability is available.
				 */
				public int modBits;
			}

			public FieldsWithGenericReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				FieldsWithGenericReplyData fieldsWithGenericReplyData = new FieldsWithGenericReplyData();
				int declaredSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				fieldsWithGenericReplyData.declared = new ArrayList<>(declaredSize);
				for (int i = 0; i < declaredSize; i++) {
					FieldsWithGenericData fieldsWithGenericReplyDataDeclared = new FieldsWithGenericData();
					fieldsWithGenericReplyDataDeclared.fieldID = mFieldID.decode(bytes, start);
					start += mFieldID.getSize();
					fieldsWithGenericReplyDataDeclared.name = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(fieldsWithGenericReplyDataDeclared.name);
					fieldsWithGenericReplyDataDeclared.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(fieldsWithGenericReplyDataDeclared.signature);
					fieldsWithGenericReplyDataDeclared.genericSignature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(fieldsWithGenericReplyDataDeclared.genericSignature);
					fieldsWithGenericReplyDataDeclared.modBits = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					fieldsWithGenericReplyData.declared.add(fieldsWithGenericReplyDataDeclared);
				}
				return fieldsWithGenericReplyData;
			}
		}

		/**
		 * Returns information, including the generic signature if any, for each method in a reference type.
		 * Inherited methodss are not included. The list of methods will include constructors (identified
		 * with the name {@code "<init>"}), the initialization method (identified with the name {@code "<clinit>"}) if
		 * present, and any synthetic methods created by the compiler. Methods are returned in the order
		 * they occur in the class file. Generic signatures are described in the signature attribute section
		 * in The Java™ Virtual Machine Specification. Since JDWP version 1.5.
		 */
		public class MethodsWithGeneric {

			/**
			 * @param refType The reference type ID.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 15);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class MethodsWithGenericReplyData {
				/**
				 * Number of declared methods.
				 */
				public List<MethodsWithGenericData> declared;
			}

			public class MethodsWithGenericData {
				/**
				 * Method ID.
				 */
				public long methodID;
				/**
				 * The name of the method.
				 */
				public String name;
				/**
				 * The JNI signature of the method.
				 */
				public String signature;
				/**
				 * The generic signature of the method, or an empty string if there is none.
				 */
				public String genericSignature;
				/**
				 * The modifier bit flags (also known as access flags) which provide additional information on the
				 * method declaration. Individual flag values are defined in Chapter 4 of The Java™ Virtual Machine
				 * Specification. In addition, The 0xf0000000 bit identifies the method as synthetic, if the
				 * synthetic attribute capability is available.
				 */
				public int modBits;
			}

			public MethodsWithGenericReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				MethodsWithGenericReplyData methodsWithGenericReplyData = new MethodsWithGenericReplyData();
				int declaredSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				methodsWithGenericReplyData.declared = new ArrayList<>(declaredSize);
				for (int i = 0; i < declaredSize; i++) {
					MethodsWithGenericData methodsWithGenericReplyDataDeclared = new MethodsWithGenericData();
					methodsWithGenericReplyDataDeclared.methodID = mMethodID.decode(bytes, start);
					start += mMethodID.getSize();
					methodsWithGenericReplyDataDeclared.name = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(methodsWithGenericReplyDataDeclared.name);
					methodsWithGenericReplyDataDeclared.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(methodsWithGenericReplyDataDeclared.signature);
					methodsWithGenericReplyDataDeclared.genericSignature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(methodsWithGenericReplyDataDeclared.genericSignature);
					methodsWithGenericReplyDataDeclared.modBits = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					methodsWithGenericReplyData.declared.add(methodsWithGenericReplyDataDeclared);
				}
				return methodsWithGenericReplyData;
			}
		}

		/**
		 * Returns instances of this reference type. Only instances that are reachable for the purposes of
		 * garbage collection are returned.
		 */
		public class Instances {

			/**
			 * @param refType      The reference type ID.
			 * @param maxInstances Maximum number of instances to return. Must be non-negative. If zero, all
			 *                     instances are returned.
			 */
			public ByteBuffer encode(long refType, int maxInstances) {
				ByteBuffer bytes = encodeCommandPacket(2, 16);
				mReferenceTypeID.encode(refType, bytes);
				JdwpInt.encode(maxInstances, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class InstancesReplyData {
				/**
				 * The number of instances that follow.
				 */
				public List<InstancesReplyDataInstances> instances;
			}

			public class InstancesReplyDataInstances {
				/**
				 * An instance of this reference type.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket instance;
			}

			public InstancesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				InstancesReplyData instancesReplyData = new InstancesReplyData();
				int instancesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				instancesReplyData.instances = new ArrayList<>(instancesSize);
				for (int i = 0; i < instancesSize; i++) {
					InstancesReplyDataInstances instancesReplyDataInstances = new InstancesReplyDataInstances();
					instancesReplyDataInstances.instance = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					instancesReplyData.instances.add(instancesReplyDataInstances);
				}
				return instancesReplyData;
			}
		}

		/**
		 * Returns the class file major and minor version numbers, as defined in the class file format of
		 * the Java Virtual Machine specification.
		 */
		public class ClassFileVersion {

			/**
			 * @param refType The class.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 17);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ClassFileVersionReplyData {
				/**
				 * Major version number
				 */
				public int majorVersion;
				/**
				 * Minor version number
				 */
				public int minorVersion;
			}

			public ClassFileVersionReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ClassFileVersionReplyData classFileVersionReplyData = new ClassFileVersionReplyData();
				classFileVersionReplyData.majorVersion = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				classFileVersionReplyData.minorVersion = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				return classFileVersionReplyData;
			}
		}

		/**
		 * Return the raw bytes of the constant pool in the format of the constant_pool item of the Class
		 * File Format in The Java™ Virtual Machine Specification.
		 */
		public class ConstantPool {

			/**
			 * @param refType The class.
			 */
			public ByteBuffer encode(long refType) {
				ByteBuffer bytes = encodeCommandPacket(2, 18);
				mReferenceTypeID.encode(refType, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ConstantPoolReplyData {
				/**
				 * Total number of constant pool entries plus one. This corresponds to the constant_pool_count item
				 * of the Class File Format in The Java™ Virtual Machine Specification.
				 */
				public int count;
				public List<ConstantPoolReplyDataBytes> bytes;
			}

			public class ConstantPoolReplyDataBytes {
				/**
				 * Raw bytes of constant pool
				 */
				public byte cpbytes;
			}

			public ConstantPoolReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ConstantPoolReplyData constantPoolReplyData = new ConstantPoolReplyData();
				constantPoolReplyData.count = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();

				int bytesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				constantPoolReplyData.bytes = new ArrayList<>(bytesSize);
				for (int i = 0; i < bytesSize; i++) {
					ConstantPoolReplyDataBytes constantPoolReplyDataBytes = new ConstantPoolReplyDataBytes();
					constantPoolReplyDataBytes.cpbytes = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					constantPoolReplyData.bytes.add(constantPoolReplyDataBytes);
				}
				return constantPoolReplyData;
			}
		}
	}

	public class ClassType {
		private final Superclass cmdSuperclass;
		private final SetValues cmdSetValues;
		private final InvokeMethod cmdInvokeMethod;
		private final NewInstance cmdNewInstance;

		public Superclass cmdSuperclass() {
			return cmdSuperclass;
		}

		public SetValues cmdSetValues() {
			return cmdSetValues;
		}

		public InvokeMethod cmdInvokeMethod() {
			return cmdInvokeMethod;
		}

		public NewInstance cmdNewInstance() {
			return cmdNewInstance;
		}

		private ClassType() {
			cmdSuperclass = new Superclass();
			cmdSetValues = new SetValues();
			cmdInvokeMethod = new InvokeMethod();
			cmdNewInstance = new NewInstance();
		}

		/**
		 * Returns the immediate superclass of a class.
		 */
		public class Superclass {

			/**
			 * @param clazz The class type ID.
			 */
			public ByteBuffer encode(long clazz) {
				ByteBuffer bytes = encodeCommandPacket(3, 1);
				mClassID.encode(clazz, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class SuperclassReplyData {
				/**
				 * The superclass (null if the class ID for java.lang.Object is specified).
				 */
				public long superclass;
			}

			public SuperclassReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				SuperclassReplyData superclassReplyData = new SuperclassReplyData();
				superclassReplyData.superclass = mClassID.decode(bytes, start);
				start += mClassID.getSize();
				return superclassReplyData;
			}
		}

		/**
		 * Sets the value of one or more static fields. Each field must be member of the class type or one
		 * of its superclasses, superinterfaces, or implemented interfaces. Access control is not enforced;
		 * for example, the values of private fields can be set. Final fields cannot be set.For primitive
		 * values, the value's type must match the field's type exactly. For object values, there must exist
		 * a widening reference conversion from the value's type to the field's type and the field's type
		 * must be loaded.
		 */
		public class SetValues {

			public class SetValuesValues {
				/**
				 * Field to set.
				 */
				public long fieldID;
				/**
				 * Value to put in the field.
				 */
				public UntaggedValuePacket value;
			}

			/**
			 * @param clazz  The class type ID.
			 * @param values The number of fields to set.
			 */
			public ByteBuffer encode(long clazz, List<SetValuesValues> values) {
				ByteBuffer bytes = encodeCommandPacket(3, 2);
				mClassID.encode(clazz, bytes);
				JdwpInt.encode(values.size(), bytes);
				for (SetValuesValues setValuesValues : values) {
					mFieldID.encode(setValuesValues.fieldID, bytes);
					mUntaggedvalue.encode(setValuesValues.value, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Invokes a static method. The method must be member of the class type or one of its superclasses,
		 * superinterfaces, or implemented interfaces. Access control is not enforced; for example, private
		 * methods can be invoked.
		 */
		public class InvokeMethod {

			public class InvokeMethodArguments {
				/**
				 * The argument value.
				 */
				public ValuePacket arg;
			}

			/**
			 * @param clazz     The class type ID.
			 * @param thread    The thread in which to invoke.
			 * @param methodID  The method to invoke.
			 * @param arguments Invocation arguments
			 * @param options   Invocation options
			 */
			public ByteBuffer encode(long clazz, long thread, long methodID, List<InvokeMethodArguments> arguments, int options)
					throws JdwpRuntimeException {
				ByteBuffer bytes = encodeCommandPacket(3, 3);
				mClassID.encode(clazz, bytes);
				mThreadID.encode(thread, bytes);
				mMethodID.encode(methodID, bytes);
				JdwpInt.encode(arguments.size(), bytes);
				for (InvokeMethodArguments invokeMethodArguments : arguments) {
					mValue.encode(invokeMethodArguments.arg, bytes);
				}
				JdwpInt.encode(options, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class InvokeMethodReplyData {
				/**
				 * The returned value.
				 */
				public ValuePacket returnValue;
				/**
				 * The thrown exception.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket exception;
			}

			public InvokeMethodReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				InvokeMethodReplyData invokeMethodReplyData = new InvokeMethodReplyData();
				invokeMethodReplyData.returnValue = mValue.decode(bytes, start);
				start += mValue.getSize(invokeMethodReplyData.returnValue.tag);
				invokeMethodReplyData.exception = mTaggedobjectID.decode(bytes, start);
				start += mTaggedobjectID.getSize();
				return invokeMethodReplyData;
			}
		}

		/**
		 * Creates a new object of this type, invoking the specified constructor. The constructor method ID
		 * must be a member of the class type.
		 */
		public class NewInstance {

			public class NewInstanceArguments {
				/**
				 * The argument value.
				 */
				public ValuePacket arg;
			}

			/**
			 * @param clazz     The class type ID.
			 * @param thread    The thread in which to invoke the constructor.
			 * @param methodID  The constructor to invoke.
			 * @param arguments Invocation arguments
			 * @param options   Constructor invocation options
			 */
			public ByteBuffer encode(long clazz, long thread, long methodID,
									 List<NewInstanceArguments> arguments, int options) throws JdwpRuntimeException {
				ByteBuffer bytes = encodeCommandPacket(3, 4);
				mClassID.encode(clazz, bytes);
				mThreadID.encode(thread, bytes);
				mMethodID.encode(methodID, bytes);
				JdwpInt.encode(arguments.size(), bytes);
				for (NewInstanceArguments newInstanceArguments : arguments) {
					mValue.encode(newInstanceArguments.arg, bytes);
				}
				JdwpInt.encode(options, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class NewInstanceReplyData {
				/**
				 * The newly created object, or null if the constructor threw an exception.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket newObject;
				/**
				 * The thrown exception, if any; otherwise, null.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket exception;
			}

			public NewInstanceReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				NewInstanceReplyData newInstanceReplyData = new NewInstanceReplyData();
				newInstanceReplyData.newObject = mTaggedobjectID.decode(bytes, start);
				start += mTaggedobjectID.getSize();
				newInstanceReplyData.exception = mTaggedobjectID.decode(bytes, start);
				start += mTaggedobjectID.getSize();
				return newInstanceReplyData;
			}
		}
	}

	public class ArrayType {
		private final NewInstance cmdNewInstance;

		public NewInstance cmdNewInstance() {
			return cmdNewInstance;
		}

		private ArrayType() {
			cmdNewInstance = new NewInstance();
		}

		/**
		 * Creates a new array object of this type with a given length.
		 */
		public class NewInstance {

			/**
			 * @param arrType The array type of the new instance.
			 * @param length  The length of the array.
			 */
			public ByteBuffer encode(long arrType, int length) {
				ByteBuffer bytes = encodeCommandPacket(4, 1);
				mArrayTypeID.encode(arrType, bytes);
				JdwpInt.encode(length, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class NewInstanceReplyData {
				/**
				 * The newly created array object.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket newArray;
			}

			public NewInstanceReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				NewInstanceReplyData newInstanceReplyData = new NewInstanceReplyData();
				newInstanceReplyData.newArray = mTaggedobjectID.decode(bytes, start);
				start += mTaggedobjectID.getSize();
				return newInstanceReplyData;
			}
		}
	}

	public static class InterfaceType {
		private InterfaceType() {
		}
	}

	public class Method {
		public final LineTable cmdLineTable;
		public final VariableTable cmdVariableTable;
		public final Bytecodes cmdBytecodes;
		public final IsObsolete cmdIsObsolete;
		public final VariableTableWithGeneric cmdVariableTableWithGeneric;

		public LineTable cmdLineTable() {
			return cmdLineTable;
		}

		public VariableTable cmdVariableTable() {
			return cmdVariableTable;
		}

		public Bytecodes cmdBytecodes() {
			return cmdBytecodes;
		}

		public IsObsolete cmdIsObsolete() {
			return cmdIsObsolete;
		}

		public VariableTableWithGeneric cmdVariableTableWithGeneric() {
			return cmdVariableTableWithGeneric;
		}

		private Method() {
			cmdLineTable = new LineTable();
			cmdVariableTable = new VariableTable();
			cmdBytecodes = new Bytecodes();
			cmdIsObsolete = new IsObsolete();
			cmdVariableTableWithGeneric = new VariableTableWithGeneric();
		}

		/**
		 * Returns line number information for the method, if present. The line table maps source line
		 * numbers to the initial code index of the line. The line table is ordered by code index (from
		 * lowest to highest). The line number information is constant unless a new class definition is
		 * installed using RedefineClasses.
		 */
		public class LineTable {

			/**
			 * @param refType  The class.
			 * @param methodID The method.
			 */
			public ByteBuffer encode(long refType, long methodID) {
				ByteBuffer bytes = encodeCommandPacket(6, 1);
				mReferenceTypeID.encode(refType, bytes);
				mMethodID.encode(methodID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class LineTableReplyData {
				/**
				 * Lowest valid code index for the method, {@code >=0}, or -1 if the method is native
				 */
				public long start;
				/**
				 * Highest valid code index for the method, {@code >=0}, or -1 if the method is native
				 */
				public long end;
				/**
				 * The number of entries in the line table for this method.
				 */
				public List<LineTableReplyDataLines> lines;
			}

			public class LineTableReplyDataLines {
				/**
				 * Initial code index of the line, {@code start <= lineCodeIndex < end}
				 */
				public long lineCodeIndex;
				/**
				 * Line number.
				 */
				public int lineNumber;
			}

			public LineTableReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				LineTableReplyData lineTableReplyData = new LineTableReplyData();
				lineTableReplyData.start = JdwpLong.decode(bytes, start);
				start += JdwpLong.getSize();
				lineTableReplyData.end = JdwpLong.decode(bytes, start);
				start += JdwpLong.getSize();

				int linesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				lineTableReplyData.lines = new ArrayList<>(linesSize);
				for (int i = 0; i < linesSize; i++) {
					LineTableReplyDataLines lineTableReplyDataLines = new LineTableReplyDataLines();
					lineTableReplyDataLines.lineCodeIndex = JdwpLong.decode(bytes, start);
					start += JdwpLong.getSize();
					lineTableReplyDataLines.lineNumber = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					lineTableReplyData.lines.add(lineTableReplyDataLines);
				}
				return lineTableReplyData;
			}
		}

		/**
		 * Returns variable information for the method. The variable table includes arguments and locals
		 * declared within the method. For instance methods, the "this" reference is included in the table.
		 * Also, synthetic variables may be present.
		 */
		public class VariableTable {

			/**
			 * @param refType  The class.
			 * @param methodID The method.
			 */
			public ByteBuffer encode(long refType, long methodID) {
				ByteBuffer bytes = encodeCommandPacket(6, 2);
				mReferenceTypeID.encode(refType, bytes);
				mMethodID.encode(methodID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class VariableTableReplyData {
				/**
				 * The number of words in the frame used by arguments. Eight-byte arguments use two words; all
				 * others use one.
				 */
				public int argCnt;
				/**
				 * The number of variables.
				 */
				public List<VariableTableReplyDataSlots> slots;
			}

			public class VariableTableReplyDataSlots {
				/**
				 * First code index at which the variable is visible (unsigned). Used in conjunction with length.
				 * The variable can be get or set only when the current
				 * {@code codeIndex <= current frame code index < codeIndex + length}
				 */
				public long codeIndex;
				/**
				 * The variable's name.
				 */
				public String name;
				/**
				 * The variable type's JNI signature.
				 */
				public String signature;
				/**
				 * Unsigned value used in conjunction with codeIndex. The variable can be get or set only when the
				 * current {@code codeIndex <= current frame code index < code index + length}
				 */
				public int length;
				/**
				 * The local variable's index in its frame
				 */
				public int slot;
			}

			public VariableTableReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				VariableTableReplyData variableTableReplyData = new VariableTableReplyData();
				variableTableReplyData.argCnt = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();

				int slotsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				variableTableReplyData.slots = new ArrayList<>(slotsSize);
				for (int i = 0; i < slotsSize; i++) {
					VariableTableReplyDataSlots variableTableReplyDataSlots = new VariableTableReplyDataSlots();
					variableTableReplyDataSlots.codeIndex = JdwpLong.decode(bytes, start);
					start += JdwpLong.getSize();
					variableTableReplyDataSlots.name = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(variableTableReplyDataSlots.name);
					variableTableReplyDataSlots.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(variableTableReplyDataSlots.signature);
					variableTableReplyDataSlots.length = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					variableTableReplyDataSlots.slot = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					variableTableReplyData.slots.add(variableTableReplyDataSlots);
				}
				return variableTableReplyData;
			}
		}

		/**
		 * Retrieve the method's bytecodes as defined in The Java™ Virtual Machine Specification. Requires
		 * canGetBytecodes capability - see CapabilitiesNew.
		 */
		public class Bytecodes {

			/**
			 * @param refType  The class.
			 * @param methodID The method.
			 */
			public ByteBuffer encode(long refType, long methodID) {
				ByteBuffer bytes = encodeCommandPacket(6, 3);
				mReferenceTypeID.encode(refType, bytes);
				mMethodID.encode(methodID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class BytecodesReplyData {
				public List<BytecodesReplyDataBytes> bytes;
			}

			public class BytecodesReplyDataBytes {
				/**
				 * A Java bytecode.
				 */
				public byte bytecode;
			}

			public BytecodesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				BytecodesReplyData bytecodesReplyData = new BytecodesReplyData();
				int bytesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				bytecodesReplyData.bytes = new ArrayList<>(bytesSize);
				for (int i = 0; i < bytesSize; i++) {
					BytecodesReplyDataBytes bytecodesReplyDataBytes = new BytecodesReplyDataBytes();
					bytecodesReplyDataBytes.bytecode = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					bytecodesReplyData.bytes.add(bytecodesReplyDataBytes);
				}
				return bytecodesReplyData;
			}
		}

		/**
		 * Determine if this method is obsolete. A method is obsolete if it has been replaced by a
		 * non-equivalent method using the RedefineClasses command. The original and redefined methods are
		 * considered equivalent if their bytecodes are the same except for indices into the constant pool
		 * and the referenced constants are equal.
		 */
		public class IsObsolete {

			/**
			 * @param refType  The class.
			 * @param methodID The method.
			 */
			public ByteBuffer encode(long refType, long methodID) {
				ByteBuffer bytes = encodeCommandPacket(6, 4);
				mReferenceTypeID.encode(refType, bytes);
				mMethodID.encode(methodID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class IsObsoleteReplyData {
				/**
				 * true if this method has been replacedby a non-equivalent method usingthe RedefineClasses command.
				 */
				public boolean isObsolete;
			}

			public IsObsoleteReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				IsObsoleteReplyData isObsoleteReplyData = new IsObsoleteReplyData();
				isObsoleteReplyData.isObsolete = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				return isObsoleteReplyData;
			}
		}

		/**
		 * Returns variable information for the method, including generic signatures for the variables. The
		 * variable table includes arguments and locals declared within the method. For instance methods,
		 * the "this" reference is included in the table. Also, synthetic variables may be present. Generic
		 * signatures are described in the signature attribute section in The Java™ Virtual Machine
		 * Specification. Since JDWP version 1.5.
		 */
		public class VariableTableWithGeneric {

			/**
			 * @param refType  The class.
			 * @param methodID The method.
			 */
			public ByteBuffer encode(long refType, long methodID) {
				ByteBuffer bytes = encodeCommandPacket(6, 5);
				mReferenceTypeID.encode(refType, bytes);
				mMethodID.encode(methodID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class VarTableWithGenericData {
				/**
				 * The number of words in the frame used by arguments. Eight-byte arguments use two words; all
				 * others use one.
				 */
				public int argCnt;
				/**
				 * The number of variables.
				 */
				public List<VarWithGenericSlot> slots;
			}

			public class VarWithGenericSlot {
				/**
				 * First code index at which the variable is visible (unsigned). Used in conjunction with length.
				 * The variable can be get or set only when the current
				 * {@code codeIndex <= current frame code index < codeIndex + length}
				 */
				public long codeIndex;
				/**
				 * The variable's name.
				 */
				public String name;
				/**
				 * The variable type's JNI signature.
				 */
				public String signature;
				/**
				 * The variable type's generic signature or an empty string if there is none.
				 */
				public String genericSignature;
				/**
				 * Unsigned value used in conjunction with codeIndex. The variable can be get or set only when the
				 * current {@code codeIndex <= current frame code index < code index + length}
				 */
				public int length;
				/**
				 * The local variable's index in its frame
				 */
				public int slot;
			}

			public VarTableWithGenericData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				VarTableWithGenericData variableTableWithGenericReplyData = new VarTableWithGenericData();
				variableTableWithGenericReplyData.argCnt = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();

				int slotsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				variableTableWithGenericReplyData.slots = new ArrayList<>(slotsSize);
				for (int i = 0; i < slotsSize; i++) {
					VarWithGenericSlot variableTableWithGenericReplyDataSlots =
							new VarWithGenericSlot();
					variableTableWithGenericReplyDataSlots.codeIndex = JdwpLong.decode(bytes, start);
					start += JdwpLong.getSize();
					variableTableWithGenericReplyDataSlots.name = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(variableTableWithGenericReplyDataSlots.name);
					variableTableWithGenericReplyDataSlots.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(variableTableWithGenericReplyDataSlots.signature);
					variableTableWithGenericReplyDataSlots.genericSignature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(variableTableWithGenericReplyDataSlots.genericSignature);
					variableTableWithGenericReplyDataSlots.length = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					variableTableWithGenericReplyDataSlots.slot = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					variableTableWithGenericReplyData.slots.add(variableTableWithGenericReplyDataSlots);
				}
				return variableTableWithGenericReplyData;
			}
		}
	}

	public static class Field {
		private Field() {
		}
	}

	public class ObjectReference {
		private final ReferenceType cmdReferenceType;
		private final GetValues cmdGetValues;
		private final SetValues cmdSetValues;
		private final MonitorInfo cmdMonitorInfo;
		private final InvokeMethod cmdInvokeMethod;
		private final DisableCollection cmdDisableCollection;
		private final EnableCollection cmdEnableCollection;
		private final IsCollected cmdIsCollected;
		private final ReferringObjects cmdReferringObjects;

		public ReferenceType cmdReferenceType() {
			return cmdReferenceType;
		}

		public GetValues cmdGetValues() {
			return cmdGetValues;
		}

		public SetValues cmdSetValues() {
			return cmdSetValues;
		}

		public MonitorInfo cmdMonitorInfo() {
			return cmdMonitorInfo;
		}

		public InvokeMethod cmdInvokeMethod() {
			return cmdInvokeMethod;
		}

		public DisableCollection cmdDisableCollection() {
			return cmdDisableCollection;
		}

		public EnableCollection cmdEnableCollection() {
			return cmdEnableCollection;
		}

		public IsCollected cmdIsCollected() {
			return cmdIsCollected;
		}

		public ReferringObjects cmdReferringObjects() {
			return cmdReferringObjects;
		}

		private ObjectReference() {
			cmdReferenceType = new ReferenceType();
			cmdGetValues = new GetValues();
			cmdSetValues = new SetValues();
			cmdMonitorInfo = new MonitorInfo();
			cmdInvokeMethod = new InvokeMethod();
			cmdDisableCollection = new DisableCollection();
			cmdEnableCollection = new EnableCollection();
			cmdIsCollected = new IsCollected();
			cmdReferringObjects = new ReferringObjects();
		}

		/**
		 * Returns the runtime type of the object. The runtime type will be a class or an array.
		 */
		public class ReferenceType {

			/**
			 * @param object The object ID
			 */
			public ByteBuffer encode(long object) {
				ByteBuffer bytes = encodeCommandPacket(9, 1);
				mObjectID.encode(object, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ReferenceTypeReplyData {
				/**
				 * Kind of following reference type.
				 */
				public byte refTypeTag;
				/**
				 * The runtime reference type.
				 */
				public long typeID;
			}

			public ReferenceTypeReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ReferenceTypeReplyData referenceTypeReplyData = new ReferenceTypeReplyData();
				referenceTypeReplyData.refTypeTag = JdwpByte.decode(bytes, start);
				start += JdwpByte.getSize();
				referenceTypeReplyData.typeID = mReferenceTypeID.decode(bytes, start);
				start += mReferenceTypeID.getSize();
				return referenceTypeReplyData;
			}
		}

		/**
		 * Returns the value of one or more instance fields. Each field must be member of the object's type
		 * or one of its superclasses, superinterfaces, or implemented interfaces. Access control is not
		 * enforced; for example, the values of private fields can be obtained.
		 */
		public class GetValues {

			public class GetValuesFields {
				/**
				 * Field to get.
				 */
				public long fieldID;
			}

			/**
			 * @param object The object ID
			 * @param fields The number of values to get
			 */
			public ByteBuffer encode(long object, List<Long> fields) {
				ByteBuffer bytes = encodeCommandPacket(9, 2);
				mObjectID.encode(object, bytes);
				JdwpInt.encode(fields.size(), bytes);
				for (Long id : fields) {
					mFieldID.encode(id, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public class GetValuesReplyData {
				/**
				 * The number of values returned, always equal to 'fields', the number of values to get. Field
				 * values are ordered in the reply in the same order as corresponding fieldIDs in the command.
				 */
				public List<GetValuesReplyDataValues> values;
			}

			public class GetValuesReplyDataValues {
				/**
				 * The field value
				 */
				public ValuePacket value;
			}

			public GetValuesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				GetValuesReplyData getValuesReplyData = new GetValuesReplyData();
				int valuesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				getValuesReplyData.values = new ArrayList<>(valuesSize);
				for (int i = 0; i < valuesSize; i++) {
					GetValuesReplyDataValues getValuesReplyDataValues = new GetValuesReplyDataValues();
					getValuesReplyDataValues.value = mValue.decode(bytes, start);
					start += mValue.getSize(getValuesReplyDataValues.value.tag);
					getValuesReplyData.values.add(getValuesReplyDataValues);
				}
				return getValuesReplyData;
			}
		}

		/**
		 * Sets the value of one or more instance fields. Each field must be member of the object's type or
		 * one of its superclasses, superinterfaces, or implemented interfaces. Access control is not
		 * enforced; for example, the values of private fields can be set. For primitive values, the value's
		 * type must match the field's type exactly. For object values, there must be a widening reference
		 * conversion from the value's type to the field's type and the field's type must be loaded.
		 */
		public class SetValues {

			public class FieldValueSetter {
				/**
				 * Field to set.
				 */
				public long fieldID;
				/**
				 * Value to put in the field.
				 */
				public UntaggedValuePacket value;
			}

			/**
			 * @param object The object ID
			 * @param values The number of fields to set.
			 */
			public ByteBuffer encode(long object, List<FieldValueSetter> values) {
				ByteBuffer bytes = encodeCommandPacket(9, 3);
				mObjectID.encode(object, bytes);
				JdwpInt.encode(values.size(), bytes);
				for (FieldValueSetter setValuesValues : values) {
					mFieldID.encode(setValuesValues.fieldID, bytes);
					mUntaggedvalue.encode(setValuesValues.value, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Returns monitor information for an object. All threads int the VM must be suspended.Requires
		 * canGetMonitorInfo capability - see CapabilitiesNew.
		 */
		public class MonitorInfo {

			/**
			 * @param object The object ID
			 */
			public ByteBuffer encode(long object) {
				ByteBuffer bytes = encodeCommandPacket(9, 5);
				mObjectID.encode(object, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class MonitorInfoReplyData {
				/**
				 * The monitor owner, or null if it is not currently owned.
				 */
				public long owner;
				/**
				 * The number of times the monitor has been entered.
				 */
				public int entryCount;
				/**
				 * The number of threads that are waiting for the monitor 0 if there is no current owner
				 */
				public List<MonitorInfoReplyDataWaiters> waiters;
			}

			public class MonitorInfoReplyDataWaiters {
				/**
				 * A thread waiting for this monitor.
				 */
				public long thread;
			}

			public MonitorInfoReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				MonitorInfoReplyData monitorInfoReplyData = new MonitorInfoReplyData();
				monitorInfoReplyData.owner = mThreadID.decode(bytes, start);
				start += mThreadID.getSize();
				monitorInfoReplyData.entryCount = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();

				int waitersSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				monitorInfoReplyData.waiters = new ArrayList<>(waitersSize);
				for (int i = 0; i < waitersSize; i++) {
					MonitorInfoReplyDataWaiters monitorInfoReplyDataWaiters = new MonitorInfoReplyDataWaiters();
					monitorInfoReplyDataWaiters.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					monitorInfoReplyData.waiters.add(monitorInfoReplyDataWaiters);
				}
				return monitorInfoReplyData;
			}
		}

		/**
		 * Invokes a instance method. The method must be member of the object's type or one of its
		 * superclasses, superinterfaces, or implemented interfaces. Access control is not enforced; for
		 * example, private methods can be invoked.
		 */
		public class InvokeMethod {

			public class InvokeMethodArguments {
				/**
				 * The argument value.
				 */
				public ValuePacket arg;
			}

			/**
			 * @param object    The object ID
			 * @param thread    The thread in which to invoke.
			 * @param clazz     The class type.
			 * @param methodID  The method to invoke.
			 * @param arguments The number of arguments.
			 * @param options   Invocation options
			 */
			public ByteBuffer encode(long object, long thread, long clazz, long methodID, List<InvokeMethodArguments> arguments,
									 int options) throws JdwpRuntimeException {
				ByteBuffer bytes = encodeCommandPacket(9, 6);
				mObjectID.encode(object, bytes);
				mThreadID.encode(thread, bytes);
				mClassID.encode(clazz, bytes);
				mMethodID.encode(methodID, bytes);
				JdwpInt.encode(arguments.size(), bytes);
				for (InvokeMethodArguments invokeMethodArguments : arguments) {
					mValue.encode(invokeMethodArguments.arg, bytes);
				}
				JdwpInt.encode(options, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class InvokeMethodReplyData {
				/**
				 * The returned value, or null if an exception is thrown.
				 */
				public ValuePacket returnValue;
				/**
				 * The thrown exception, if any.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket exception;
			}

			public InvokeMethodReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				InvokeMethodReplyData invokeMethodReplyData = new InvokeMethodReplyData();
				invokeMethodReplyData.returnValue = mValue.decode(bytes, start);
				start += mValue.getSize(invokeMethodReplyData.returnValue.tag);
				invokeMethodReplyData.exception = mTaggedobjectID.decode(bytes, start);
				start += mTaggedobjectID.getSize();
				return invokeMethodReplyData;
			}
		}

		/**
		 * Prevents garbage collection for the given object. By default all objects in back-end replies may
		 * be collected at any time the target VM is running. A call to this command guarantees that the
		 * object will not be collected. The EnableCollection command can be used to allow collection once
		 * again.
		 */
		public class DisableCollection {

			/**
			 * @param object The object ID
			 */
			public ByteBuffer encode(long object) {
				ByteBuffer bytes = encodeCommandPacket(9, 7);
				mObjectID.encode(object, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Permits garbage collection for this object. By default all objects returned by JDWP may become
		 * unreachable in the target VM, and hence may be garbage collected. A call to this command is
		 * necessary only if garbage collection was previously disabled with the DisableCollection command.
		 */
		public class EnableCollection {

			/**
			 * @param object The object ID
			 */
			public ByteBuffer encode(long object) {
				ByteBuffer bytes = encodeCommandPacket(9, 8);
				mObjectID.encode(object, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Determines whether an object has been garbage collected in the target VM.
		 */
		public class IsCollected {

			/**
			 * @param object The object ID
			 */
			public ByteBuffer encode(long object) {
				ByteBuffer bytes = encodeCommandPacket(9, 9);
				mObjectID.encode(object, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class IsCollectedReplyData {
				/**
				 * true if the object has been collected; false otherwise
				 */
				public boolean isCollected;
			}

			public IsCollectedReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				IsCollectedReplyData isCollectedReplyData = new IsCollectedReplyData();
				isCollectedReplyData.isCollected = JdwpBoolean.decode(bytes, start);
				start += JdwpBoolean.getSize();
				return isCollectedReplyData;
			}
		}

		/**
		 * Returns objects that directly reference this object. Only objects that are reachable for the
		 * purposes of garbage collection are returned. Note that an object can also be referenced in other
		 * ways, such as from a local variable in a stack frame, or from a JNI global reference. Such
		 * non-object referrers are not returned by this command.
		 */
		public class ReferringObjects {

			/**
			 * @param object       The object ID
			 * @param maxReferrers Maximum number of referring objects to return. Must be non-negative. If zero,
			 *                     all referring objects are returned.
			 */
			public ByteBuffer encode(long object, int maxReferrers) {
				ByteBuffer bytes = encodeCommandPacket(9, 10);
				mObjectID.encode(object, bytes);
				JdwpInt.encode(maxReferrers, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ReferringObjectsReplyData {
				/**
				 * The number of objects that follow.
				 */
				public List<ReferringObjectsReplyDataReferringObjects> referringObjects;
			}

			public class ReferringObjectsReplyDataReferringObjects {
				/**
				 * An object that references this object.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket instance;
			}

			public ReferringObjectsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ReferringObjectsReplyData referringObjectsReplyData = new ReferringObjectsReplyData();
				int referringObjectsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				referringObjectsReplyData.referringObjects = new ArrayList<>(referringObjectsSize);
				for (int i = 0; i < referringObjectsSize; i++) {
					ReferringObjectsReplyDataReferringObjects referringObjectsReplyDataReferringObjects =
							new ReferringObjectsReplyDataReferringObjects();
					referringObjectsReplyDataReferringObjects.instance = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					referringObjectsReplyData.referringObjects.add(referringObjectsReplyDataReferringObjects);
				}
				return referringObjectsReplyData;
			}
		}
	}

	public class StringReference {
		private final Value cmdValue;

		public Value cmdValue() {
			return cmdValue;
		}

		private StringReference() {
			cmdValue = new Value();
		}

		/**
		 * Returns the characters contained in the string.
		 */
		public class Value {

			/**
			 * @param stringObject The String object ID.
			 */
			public ByteBuffer encode(long stringObject) {
				ByteBuffer bytes = encodeCommandPacket(10, 1);
				mObjectID.encode(stringObject, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ValueReplyData {
				/**
				 * UTF-8 representation of the string value.
				 */
				public String stringValue;
			}

			public ValueReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ValueReplyData valueReplyData = new ValueReplyData();
				valueReplyData.stringValue = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(valueReplyData.stringValue);
				return valueReplyData;
			}
		}
	}

	public class ThreadReference {
		private final Name cmdName;
		private final Suspend cmdSuspend;
		private final Resume cmdResume;
		private final Status cmdStatus;
		private final ThreadGroup cmdThreadGroup;
		private final Frames cmdFrames;
		private final FrameCount cmdFrameCount;
		private final OwnedMonitors cmdOwnedMonitors;
		private final CurrentContendedMonitor cmdCurrentContendedMonitor;
		private final Stop cmdStop;
		private final Interrupt cmdInterrupt;
		private final SuspendCount cmdSuspendCount;
		private final OwnedMonitorsStackDepthInfo cmdOwnedMonitorsStackDepthInfo;
		private final ForceEarlyReturn cmdForceEarlyReturn;

		public Name cmdName() {
			return cmdName;
		}

		public Suspend cmdSuspend() {
			return cmdSuspend;
		}

		public Resume cmdResume() {
			return cmdResume;
		}

		public Status cmdStatus() {
			return cmdStatus;
		}

		public ThreadGroup cmdThreadGroup() {
			return cmdThreadGroup;
		}

		public Frames cmdFrames() {
			return cmdFrames;
		}

		public FrameCount cmdFrameCount() {
			return cmdFrameCount;
		}

		public OwnedMonitors cmdOwnedMonitors() {
			return cmdOwnedMonitors;
		}

		public CurrentContendedMonitor cmdCurrentContendedMonitor() {
			return cmdCurrentContendedMonitor;
		}

		public Stop cmdStop() {
			return cmdStop;
		}

		public Interrupt cmdInterrupt() {
			return cmdInterrupt;
		}

		public SuspendCount cmdSuspendCount() {
			return cmdSuspendCount;
		}

		public OwnedMonitorsStackDepthInfo cmdOwnedMonitorsStackDepthInfo() {
			return cmdOwnedMonitorsStackDepthInfo;
		}

		public ForceEarlyReturn cmdForceEarlyReturn() {
			return cmdForceEarlyReturn;
		}

		private ThreadReference() {
			cmdName = new Name();
			cmdSuspend = new Suspend();
			cmdResume = new Resume();
			cmdStatus = new Status();
			cmdThreadGroup = new ThreadGroup();
			cmdFrames = new Frames();
			cmdFrameCount = new FrameCount();
			cmdOwnedMonitors = new OwnedMonitors();
			cmdCurrentContendedMonitor = new CurrentContendedMonitor();
			cmdStop = new Stop();
			cmdInterrupt = new Interrupt();
			cmdSuspendCount = new SuspendCount();
			cmdOwnedMonitorsStackDepthInfo = new OwnedMonitorsStackDepthInfo();
			cmdForceEarlyReturn = new ForceEarlyReturn();
		}

		/**
		 * Returns the thread name.
		 */
		public class Name {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 1);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class NameReplyData {
				/**
				 * The thread name.
				 */
				public String threadName;
			}

			public NameReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				NameReplyData nameReplyData = new NameReplyData();
				nameReplyData.threadName = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(nameReplyData.threadName);
				return nameReplyData;
			}
		}

		/**
		 * Suspends the thread.
		 */
		public class Suspend {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 2);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Resumes the execution of a given thread. If this thread was not previously suspended by the
		 * front-end, calling this command has no effect. Otherwise, the count of pending suspends on this
		 * thread is decremented. If it is decremented to 0, the thread will continue to execute.
		 */
		public class Resume {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 3);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Returns the current status of a thread. The thread status reply indicates the thread status the
		 * last time it was running. the suspend status provides information on the thread's suspension, if
		 * any.
		 */
		public class Status {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 4);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class StatusReplyData {
				/**
				 * One of the thread status codes See JDWP.ThreadStatus
				 */
				public int threadStatus;
				/**
				 * One of the suspend status codes See JDWP.SuspendStatus
				 */
				public int suspendStatus;
			}

			public StatusReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				StatusReplyData statusReplyData = new StatusReplyData();
				statusReplyData.threadStatus = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				statusReplyData.suspendStatus = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				return statusReplyData;
			}
		}

		/**
		 * Returns the thread group that contains a given thread.
		 */
		public class ThreadGroup {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 5);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ThreadGroupReplyData {
				/**
				 * The thread group of this thread.
				 */
				public long group;
			}

			public ThreadGroupReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ThreadGroupReplyData threadGroupReplyData = new ThreadGroupReplyData();
				threadGroupReplyData.group = mThreadGroupID.decode(bytes, start);
				start += mThreadGroupID.getSize();
				return threadGroupReplyData;
			}
		}

		/**
		 * Returns the current call stack of a suspended thread. The sequence of frames starts with the
		 * currently executing frame, followed by its caller, and so on. The thread must be suspended, and
		 * the returned frameID is valid only while the thread is suspended.
		 */
		public class Frames {

			/**
			 * @param thread     The thread object ID.
			 * @param startFrame The index of the first frame to retrieve.
			 * @param length     The count of frames to retrieve (-1 means all remaining).
			 */
			public ByteBuffer encode(long thread, int startFrame, int length) {
				ByteBuffer bytes = encodeCommandPacket(11, 6);
				mThreadID.encode(thread, bytes);
				JdwpInt.encode(startFrame, bytes);
				JdwpInt.encode(length, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class FramesReplyData {
				/**
				 * The number of frames retreived
				 */
				public List<FramesReplyDataFrames> frames;
			}

			public class FramesReplyDataFrames {
				/**
				 * The ID of this frame.
				 */
				public long frameID;
				/**
				 * The current location of this frame
				 */
				public JdwpLocation.LocationPacket location;
			}

			public FramesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				FramesReplyData framesReplyData = new FramesReplyData();
				int framesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				framesReplyData.frames = new ArrayList<>(framesSize);
				for (int i = 0; i < framesSize; i++) {
					FramesReplyDataFrames framesReplyDataFrames = new FramesReplyDataFrames();
					framesReplyDataFrames.frameID = mFrameID.decode(bytes, start);
					start += mFrameID.getSize();
					framesReplyDataFrames.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					framesReplyData.frames.add(framesReplyDataFrames);
				}
				return framesReplyData;
			}
		}

		/**
		 * Returns the count of frames on this thread's stack. The thread must be suspended, and the
		 * returned count is valid only while the thread is suspended. Returns
		 * JDWP.Error.errorThreadNotSuspended if not suspended.
		 */
		public class FrameCount {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 7);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class FrameCountReplyData {
				/**
				 * The count of frames on this thread's stack.
				 */
				public int frameCount;
			}

			public FrameCountReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				FrameCountReplyData frameCountReplyData = new FrameCountReplyData();
				frameCountReplyData.frameCount = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				return frameCountReplyData;
			}
		}

		/**
		 * Returns the objects whose monitors have been entered by this thread. The thread must be
		 * suspended, and the returned information is relevant only while the thread is suspended. Requires
		 * canGetOwnedMonitorInfo capability - see CapabilitiesNew.
		 */
		public class OwnedMonitors {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 8);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class OwnedMonitorsReplyData {
				/**
				 * The number of owned monitors
				 */
				public List<OwnedMonitorsReplyDataOwned> owned;
			}

			public class OwnedMonitorsReplyDataOwned {
				/**
				 * An owned monitor
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket monitor;
			}

			public OwnedMonitorsReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				OwnedMonitorsReplyData ownedMonitorsReplyData = new OwnedMonitorsReplyData();
				int ownedSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				ownedMonitorsReplyData.owned = new ArrayList<>(ownedSize);
				for (int i = 0; i < ownedSize; i++) {
					OwnedMonitorsReplyDataOwned ownedMonitorsReplyDataOwned = new OwnedMonitorsReplyDataOwned();
					ownedMonitorsReplyDataOwned.monitor = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					ownedMonitorsReplyData.owned.add(ownedMonitorsReplyDataOwned);
				}
				return ownedMonitorsReplyData;
			}
		}

		/**
		 * Returns the object, if any, for which this thread is waiting. The thread may be waiting to enter
		 * a monitor, or it may be waiting, via the java.lang.Object.wait method, for another thread to
		 * invoke the notify method. The thread must be suspended, and the returned information is relevant
		 * only while the thread is suspended. Requires canGetCurrentContendedMonitor capability - see
		 * CapabilitiesNew.
		 */
		public class CurrentContendedMonitor {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 9);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class CurrentContendedMonitorReplyData {
				/**
				 * The contended monitor, or null if there is no current contended monitor.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket monitor;
			}

			public CurrentContendedMonitorReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				CurrentContendedMonitorReplyData currentContendedMonitorReplyData = new CurrentContendedMonitorReplyData();
				currentContendedMonitorReplyData.monitor = mTaggedobjectID.decode(bytes, start);
				start += mTaggedobjectID.getSize();
				return currentContendedMonitorReplyData;
			}
		}

		/**
		 * Stops the thread with an asynchronous exception, as if done by java.lang.Thread.stop
		 */
		public class Stop {

			/**
			 * @param thread    The thread object ID.
			 * @param throwable Asynchronous exception. This object must be an instance of java.lang.Throwable
			 *                  or a subclass
			 */
			public ByteBuffer encode(long thread, long throwable) {
				ByteBuffer bytes = encodeCommandPacket(11, 10);
				mThreadID.encode(thread, bytes);
				mObjectID.encode(throwable, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Interrupt the thread, as if done by java.lang.Thread.interrupt
		 */
		public class Interrupt {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 11);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Get the suspend count for this thread. The suspend count is the number of times the thread has
		 * been suspended through the thread-level or VM-level suspend commands without a corresponding
		 * resume
		 */
		public class SuspendCount {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 12);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class SuspendCountReplyData {
				/**
				 * The number of outstanding suspends of this thread.
				 */
				public int suspendCount;
			}

			public SuspendCountReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				SuspendCountReplyData suspendCountReplyData = new SuspendCountReplyData();
				suspendCountReplyData.suspendCount = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				return suspendCountReplyData;
			}
		}

		/**
		 * Returns monitor objects owned by the thread, along with stack depth at which the monitor was
		 * acquired. Returns stack depth of -1 if the implementation cannot determine the stack depth (e.g.,
		 * for monitors acquired by JNI MonitorEnter).The thread must be suspended, and the returned
		 * information is relevant only while the thread is suspended. Requires canGetMonitorFrameInfo
		 * capability - see CapabilitiesNew.
		 */
		public class OwnedMonitorsStackDepthInfo {

			/**
			 * @param thread The thread object ID.
			 */
			public ByteBuffer encode(long thread) {
				ByteBuffer bytes = encodeCommandPacket(11, 13);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class OwnedMonitorsStackDepthInfoReplyData {
				/**
				 * The number of owned monitors
				 */
				public List<OwnedMonitorsStackDepthInfoReplyDataOwned> owned;
			}

			public class OwnedMonitorsStackDepthInfoReplyDataOwned {
				/**
				 * An owned monitor
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket monitor;
				/**
				 * Stack depth location where monitor was acquired
				 */
				public int stackDepth;
			}

			public OwnedMonitorsStackDepthInfoReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				OwnedMonitorsStackDepthInfoReplyData ownedMonitorsStackDepthInfoReplyData = new OwnedMonitorsStackDepthInfoReplyData();
				int ownedSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				ownedMonitorsStackDepthInfoReplyData.owned = new ArrayList<>(ownedSize);
				for (int i = 0; i < ownedSize; i++) {
					OwnedMonitorsStackDepthInfoReplyDataOwned ownedMonitorsStackDepthInfoReplyDataOwned =
							new OwnedMonitorsStackDepthInfoReplyDataOwned();
					ownedMonitorsStackDepthInfoReplyDataOwned.monitor = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					ownedMonitorsStackDepthInfoReplyDataOwned.stackDepth = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					ownedMonitorsStackDepthInfoReplyData.owned.add(ownedMonitorsStackDepthInfoReplyDataOwned);
				}
				return ownedMonitorsStackDepthInfoReplyData;
			}
		}

		/**
		 * Force a method to return before it reaches a return statement.
		 */
		public class ForceEarlyReturn {

			/**
			 * @param thread The thread object ID.
			 * @param value  The value to return.
			 */
			public ByteBuffer encode(long thread, ValuePacket value) throws JdwpRuntimeException {
				ByteBuffer bytes = encodeCommandPacket(11, 14);
				mThreadID.encode(thread, bytes);
				mValue.encode(value, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}
	}

	public class ThreadGroupReference {
		private final Name cmdName;
		private final Parent cmdParent;
		private final Children cmdChildren;

		public Name cmdName() {
			return cmdName;
		}

		public Parent cmdParent() {
			return cmdParent;
		}

		public Children cmdChildren() {
			return cmdChildren;
		}

		private ThreadGroupReference() {
			cmdName = new Name();
			cmdParent = new Parent();
			cmdChildren = new Children();
		}

		/**
		 * Returns the thread group name.
		 */
		public class Name {

			/**
			 * @param group The thread group object ID.
			 */
			public ByteBuffer encode(long group) {
				ByteBuffer bytes = encodeCommandPacket(12, 1);
				mThreadGroupID.encode(group, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class NameReplyData {
				/**
				 * The thread group's name.
				 */
				public String groupName;
			}

			public NameReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				NameReplyData nameReplyData = new NameReplyData();
				nameReplyData.groupName = JdwpString.decode(bytes, start);
				start += JdwpString.getSize(nameReplyData.groupName);
				return nameReplyData;
			}
		}

		/**
		 * Returns the thread group, if any, which contains a given thread group.
		 */
		public class Parent {

			/**
			 * @param group The thread group object ID.
			 */
			public ByteBuffer encode(long group) {
				ByteBuffer bytes = encodeCommandPacket(12, 2);
				mThreadGroupID.encode(group, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ParentReplyData {
				/**
				 * The parent thread group object, or null if the given thread group is a top-level thread group
				 */
				public long parentGroup;
			}

			public ParentReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ParentReplyData parentReplyData = new ParentReplyData();
				parentReplyData.parentGroup = mThreadGroupID.decode(bytes, start);
				start += mThreadGroupID.getSize();
				return parentReplyData;
			}
		}

		/**
		 * Returns the live threads and active thread groups directly contained in this thread group.
		 * Threads and thread groups in child thread groups are not included. A thread is alive if it has
		 * been started and has not yet been stopped. See java.lang.ThreadGroup for information about active
		 * ThreadGroups.
		 */
		public class Children {

			/**
			 * @param group The thread group object ID.
			 */
			public ByteBuffer encode(long group) {
				ByteBuffer bytes = encodeCommandPacket(12, 3);
				mThreadGroupID.encode(group, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ChildrenReplyData {
				/**
				 * The number of live child threads.
				 */
				public List<ChildrenReplyDataChildThreads> childThreads;
			}

			public class ChildrenReplyDataChildThreads {
				/**
				 * A direct child thread ID.
				 */
				public long childThread;
				/**
				 * The number of active child thread groups.
				 */
				public List<ChildrenReplyDataChildGroups> childGroups;
			}

			public class ChildrenReplyDataChildGroups {
				/**
				 * A direct child thread group ID.
				 */
				public long childGroup;
			}

			public ChildrenReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ChildrenReplyData childrenReplyData = new ChildrenReplyData();
				int childThreadsSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				childrenReplyData.childThreads = new ArrayList<>(childThreadsSize);
				for (int i = 0; i < childThreadsSize; i++) {
					ChildrenReplyDataChildThreads childrenReplyDataChildThreads = new ChildrenReplyDataChildThreads();
					childrenReplyDataChildThreads.childThread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();

					int childGroupsSize = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					childrenReplyDataChildThreads.childGroups = new ArrayList<>(childGroupsSize);
					for (int ii = 0; ii < childGroupsSize; ii++) {
						ChildrenReplyDataChildGroups childrenReplyDataChildGroups = new ChildrenReplyDataChildGroups();
						childrenReplyDataChildGroups.childGroup = mThreadGroupID.decode(bytes, start);
						start += mThreadGroupID.getSize();
						childrenReplyDataChildThreads.childGroups.add(childrenReplyDataChildGroups);
					}
					childrenReplyData.childThreads.add(childrenReplyDataChildThreads);
				}
				return childrenReplyData;
			}
		}
	}

	public class ArrayReference {
		private final Length cmdLength;
		private final GetValues cmdGetValues;
		private final SetValues cmdSetValues;

		public Length cmdLength() {
			return cmdLength;
		}

		public GetValues cmdGetValues() {
			return cmdGetValues;
		}

		public SetValues cmdSetValues() {
			return cmdSetValues;
		}

		private ArrayReference() {
			cmdLength = new Length();
			cmdGetValues = new GetValues();
			cmdSetValues = new SetValues();
		}

		/**
		 * Returns the number of components in a given array.
		 */
		public class Length {

			/**
			 * @param arrayObject The array object ID.
			 */
			public ByteBuffer encode(long arrayObject) {
				ByteBuffer bytes = encodeCommandPacket(13, 1);
				mArrayID.encode(arrayObject, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class LengthReplyData {
				/**
				 * The length of the array.
				 */
				public int arrayLength;
			}

			public LengthReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				LengthReplyData lengthReplyData = new LengthReplyData();
				lengthReplyData.arrayLength = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				return lengthReplyData;
			}
		}

		/**
		 * Returns a range of array components. The specified range must be within the bounds of the array.
		 */
		public class GetValues {

			/**
			 * @param arrayObject The array object ID.
			 * @param firstIndex  The first index to retrieve.
			 * @param length      The number of components to retrieve.
			 */
			public ByteBuffer encode(long arrayObject, int firstIndex, int length) {
				ByteBuffer bytes = encodeCommandPacket(13, 2);
				mArrayID.encode(arrayObject, bytes);
				JdwpInt.encode(firstIndex, bytes);
				JdwpInt.encode(length, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class GetValuesReplyData {
				/**
				 * The retrieved values. If the values are objects, they are tagged-values; otherwise, they are
				 * untagged-values
				 */
				public JdwpArrayregion.ArrayRegionPacket values;
			}

			public GetValuesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				GetValuesReplyData getValuesReplyData = new GetValuesReplyData();
				getValuesReplyData.values = mArrayregion.decode(bytes, start);
				start += mArrayregion.getSize(getValuesReplyData.values.arrayLen, getValuesReplyData.values.tag);
				return getValuesReplyData;
			}
		}

		/**
		 * Sets a range of array components. The specified range must be within the bounds of the array. For
		 * primitive values, each value's type must match the array component type exactly. For object
		 * values, there must be a widening reference conversion from the value's type to the array
		 * component type and the array component type must be loaded.
		 */
		public class SetValues {

			public class SetValuesValues {
				/**
				 * A value to set.
				 */
				public UntaggedValuePacket value;
			}

			/**
			 * @param arrayObject The array object ID.
			 * @param firstIndex  The first index to set.
			 * @param values      The number of values to set.
			 */
			public ByteBuffer encode(long arrayObject, int firstIndex, List<SetValuesValues> values) {
				ByteBuffer bytes = encodeCommandPacket(13, 3);
				mArrayID.encode(arrayObject, bytes);
				JdwpInt.encode(firstIndex, bytes);
				JdwpInt.encode(values.size(), bytes);
				for (SetValuesValues setValuesValues : values) {
					mUntaggedvalue.encode(setValuesValues.value, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}
	}

	public class ClassLoaderReference {
		private final VisibleClasses cmdVisibleClasses;

		public VisibleClasses cmdVisibleClasses() {
			return cmdVisibleClasses;
		}

		private ClassLoaderReference() {
			cmdVisibleClasses = new VisibleClasses();
		}

		/**
		 * Returns a list of all classes which this class loader has been requested to load. This class
		 * loader is considered to be an initiating class loader for each class in the returned list. The
		 * list contains each reference type defined by this loader and any types for which loading was
		 * delegated by this class loader to another class loader.
		 */
		public class VisibleClasses {

			/**
			 * @param classLoaderObject The class loader object ID.
			 */
			public ByteBuffer encode(long classLoaderObject) {
				ByteBuffer bytes = encodeCommandPacket(14, 1);
				mClassLoaderID.encode(classLoaderObject, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class VisibleClassesReplyData {
				/**
				 * The number of visible classes.
				 */
				public List<VisibleClassesReplyDataClasses> classes;
			}

			public class VisibleClassesReplyDataClasses {
				/**
				 * Kind of following reference type.
				 */
				public byte refTypeTag;
				/**
				 * A class visible to this class loader.
				 */
				public long typeID;
			}

			public VisibleClassesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				VisibleClassesReplyData visibleClassesReplyData = new VisibleClassesReplyData();
				int classesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				visibleClassesReplyData.classes = new ArrayList<>(classesSize);
				for (int i = 0; i < classesSize; i++) {
					VisibleClassesReplyDataClasses visibleClassesReplyDataClasses = new VisibleClassesReplyDataClasses();
					visibleClassesReplyDataClasses.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					visibleClassesReplyDataClasses.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					visibleClassesReplyData.classes.add(visibleClassesReplyDataClasses);
				}
				return visibleClassesReplyData;
			}
		}
	}

	public class EventRequest {
		private final Set cmdSet;
		private final Clear cmdClear;
		private final ClearAllBreakpoints cmdClearAllBreakpoints;

		public Set cmdSet() {
			return cmdSet;
		}

		public Clear cmdClear() {
			return cmdClear;
		}

		public ClearAllBreakpoints cmdClearAllBreakpoints() {
			return cmdClearAllBreakpoints;
		}

		private EventRequest() {
			cmdSet = new Set();
			cmdClear = new Clear();
			cmdClearAllBreakpoints = new ClearAllBreakpoints();
		}

		/**
		 * Set an event request. When the event described by this request occurs, an event is sent from the
		 * target VM. If an event occurs that has not been requested then it is not sent from the target VM.
		 * The two exceptions to this are the VM Start Event and the VM Death Event which are automatically
		 * generated events - see Composite Command for further details.
		 */
		public class Set {
			public int decodeRequestID(byte[] bytes, int start) throws JdwpRuntimeException {
				return decodeInt(bytes, start);
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 */
			public ByteBuffer encode(byte eventKind, byte suspendPolicy, List<EventRequestEncoder> requestModifiers) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(requestModifiers.size(), bytes);
				for (EventRequestEncoder modifier : requestModifiers) {
					modifier.encode(bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param count         Count before event. One for one-off.
			 */
			public ByteBuffer newCountRequest(byte eventKind, byte suspendPolicy, int count) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(CountRequest.MOD_KIND);
				JdwpInt.encode(count, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param exprID        For the future
			 */
			public ByteBuffer newConditionalRequest(byte eventKind, byte suspendPolicy, int exprID) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(ConditionalRequest.MOD_KIND);
				JdwpInt.encode(exprID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param thread        Required thread
			 */
			public ByteBuffer newThreadOnlyRequest(byte eventKind, byte suspendPolicy, long thread) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(ThreadOnlyRequest.MOD_KIND);
				mThreadID.encode(thread, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param clazz         Required class
			 */
			public ByteBuffer newClassOnlyRequest(byte eventKind, byte suspendPolicy, long clazz) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(ClassOnlyRequest.MOD_KIND);
				mReferenceTypeID.encode(clazz, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param classPattern  Required class pattern. Matches are limited to exact matches of the given
			 *                      class pattern and matches of patterns that begin or end with '*'; for
			 *                      example, "*.Foo" or "java.*".
			 */
			public ByteBuffer newClassMatchRequest(byte eventKind, byte suspendPolicy, String classPattern) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(ClassMatchRequest.MOD_KIND);
				JdwpString.encode(classPattern, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param classPattern  Disallowed class pattern. Matches are limited to exact matches of the given
			 *                      class pattern and matches of patterns that begin or end with '*'; for
			 *                      example, "*.Foo" or "java.*".
			 */
			public ByteBuffer newClassExcludeRequest(byte eventKind, byte suspendPolicy, String classPattern) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(ClassExcludeRequest.MOD_KIND);
				JdwpString.encode(classPattern, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param loc           Required location
			 */
			public ByteBuffer newLocationOnlyRequest(byte eventKind, byte suspendPolicy, JdwpLocation.LocationPacket loc) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(LocationOnlyRequest.MOD_KIND);
				mLocation.encode(loc, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind       Event kind to request. See JDWP.EventKind for a complete list of events
			 *                        that can be requested; some events may require a capability in order to be
			 *                        requested.
			 * @param suspendPolicy   What threads are suspended when this event occurs? Note that the order of
			 *                        events and command replies accurately reflects the order in which threads
			 *                        are suspended and resumed. For example, if a VM-wide resume is processed
			 *                        before an event occurs which suspends the VM, the reply to the resume
			 *                        command will be written to the transport before the suspending event.
			 * @param exceptionOrNull Exception to report. Null (0) means report exceptions of all types. A
			 *                        non-null type restricts the reported exception events to exceptions of the
			 *                        given type or any of its subtypes.
			 * @param caught          Report caught exceptions
			 * @param uncaught        Report uncaught exceptions. Note that it is not always possible to
			 *                        determine whether an exception is caught or uncaught at the time it is
			 *                        thrown. See the exception event catch location under composite events for
			 *                        more information.
			 */
			public ByteBuffer newExceptionOnlyRequest(byte eventKind, byte suspendPolicy, long exceptionOrNull, boolean caught,
													  boolean uncaught) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(ExceptionOnlyRequest.MOD_KIND);
				mReferenceTypeID.encode(exceptionOrNull, bytes);
				JdwpBoolean.encode(caught, bytes);
				JdwpBoolean.encode(uncaught, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param declaring     Type in which field is declared.
			 * @param fieldID       Required field
			 */
			public ByteBuffer newFieldOnlyRequest(byte eventKind, byte suspendPolicy, long declaring, long fieldID) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(FieldOnlyRequest.MOD_KIND);
				mReferenceTypeID.encode(declaring, bytes);
				mFieldID.encode(fieldID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param thread        Thread in which to step
			 * @param size          size of each step. See JDWP.StepSize
			 * @param depth         relative call stack limit. See JDWP.StepDepth
			 */
			public ByteBuffer newStepRequest(byte eventKind, byte suspendPolicy, long thread, int size, int depth) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(StepRequest.MOD_KIND);
				mThreadID.encode(thread, bytes);
				JdwpInt.encode(size, bytes);
				JdwpInt.encode(depth, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind     Event kind to request. See JDWP.EventKind for a complete list of events that
			 *                      can be requested; some events may require a capability in order to be
			 *                      requested.
			 * @param suspendPolicy What threads are suspended when this event occurs? Note that the order of
			 *                      events and command replies accurately reflects the order in which threads
			 *                      are suspended and resumed. For example, if a VM-wide resume is processed
			 *                      before an event occurs which suspends the VM, the reply to the resume
			 *                      command will be written to the transport before the suspending event.
			 * @param instance      Required 'this' object
			 */
			public ByteBuffer newInstanceOnlyRequest(byte eventKind, byte suspendPolicy, long instance) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(InstanceOnlyRequest.MOD_KIND);
				mObjectID.encode(instance, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			/**
			 * @param eventKind         Event kind to request. See JDWP.EventKind for a complete list of events
			 *                          that can be requested; some events may require a capability in order to
			 *                          be requested.
			 * @param suspendPolicy     What threads are suspended when this event occurs? Note that the order
			 *                          of events and command replies accurately reflects the order in which
			 *                          threads are suspended and resumed. For example, if a VM-wide resume is
			 *                          processed before an event occurs which suspends the VM, the reply to the
			 *                          resume command will be written to the transport before the suspending
			 *                          event.
			 * @param sourceNamePattern Required source name pattern. Matches are limited to exact matches of
			 *                          the given pattern and matches of patterns that begin or end with '*';
			 *                          for example, "*.Foo" or "java.*".
			 */
			public ByteBuffer newSourceNameMatchRequest(byte eventKind, byte suspendPolicy, String sourceNamePattern) {
				ByteBuffer bytes = encodeCommandPacket(15, 1);
				JdwpByte.encode(eventKind, bytes);
				JdwpByte.encode(suspendPolicy, bytes);
				JdwpInt.encode(1, bytes);
				bytes.add(SourceNameMatchRequest.MOD_KIND);
				JdwpString.encode(sourceNamePattern, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public CountRequest newCountRequest() {
				return new CountRequest();
			}

			public ConditionalRequest newConditionalRequest() {
				return new ConditionalRequest();
			}

			public ThreadOnlyRequest newThreadOnlyRequest() {
				return new ThreadOnlyRequest();
			}

			public ClassOnlyRequest newClassOnlyRequest() {
				return new ClassOnlyRequest();
			}

			public ClassMatchRequest newClassMatchRequest() {
				return new ClassMatchRequest();
			}

			public ClassExcludeRequest newClassExcludeRequest() {
				return new ClassExcludeRequest();
			}

			public LocationOnlyRequest newLocationOnlyRequest() {
				return new LocationOnlyRequest();
			}

			public ExceptionOnlyRequest newExceptionOnlyRequest() {
				return new ExceptionOnlyRequest();
			}

			public FieldOnlyRequest newFieldOnlyRequest() {
				return new FieldOnlyRequest();
			}

			public StepRequest newStepRequest() {
				return new StepRequest();
			}

			public InstanceOnlyRequest newInstanceOnlyRequest() {
				return new InstanceOnlyRequest();
			}

			public SourceNameMatchRequest newSourceNameMatchRequest() {
				return new SourceNameMatchRequest();
			}

			/**
			 * Limit the requested event to be reported at most once after a given number of occurrences. The
			 * event is not reported the first count - 1 times this filter is reached. To request a one-off
			 * event, call this method with a count of 1.
			 * Case Count - if modKind is 1: Limit the requested event to be reported at most once after a given
			 * number of occurrences. The event is not reported the first count - 1 times this filter is
			 * reached. To request a one-off event, call this method with a count of 1.
			 * Once the count reaches 0, any subsequent filters in this request are applied. If none of those
			 * filters cause the event to be suppressed, the event is reported. Otherwise, the event is not
			 * reported. In either case subsequent events are never reported for this request. This modifier can
			 * be used with any event kind.
			 */
			public class CountRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 1;
				/**
				 * Count before event. One for one-off.
				 */
				public int count;

				public void encode(ByteBuffer bytes) {
					bytes.add(CountRequest.MOD_KIND);
					JdwpInt.encode(count, bytes);
				}
			}

			/**
			 * Conditional on expression
			 */
			public class ConditionalRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 2;
				/**
				 * For the future
				 */
				public int exprID;

				public void encode(ByteBuffer bytes) {
					bytes.add(ConditionalRequest.MOD_KIND);
					JdwpInt.encode(exprID, bytes);
				}
			}

			/**
			 * Restricts reported events to those in the given thread. This modifier can be used with any event
			 * kind except for class unload.
			 */
			public class ThreadOnlyRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 3;
				/**
				 * Required thread
				 */
				public long thread;

				public void encode(ByteBuffer bytes) {
					bytes.add(ThreadOnlyRequest.MOD_KIND);
					mThreadID.encode(thread, bytes);
				}
			}

			/**
			 * For class prepare events, restricts the events generated by this request to be the preparation of
			 * the given reference type and any subtypes. For monitor wait and waited events, restricts the
			 * events generated by this request to those whose monitor object is of the given reference type or
			 * any of its subtypes. For other events, restricts the events generated by this request to those
			 * whose location is in the given reference type or any of its subtypes. An event will be generated
			 * for any location in a reference type that can be safely cast to the given reference type. This
			 * modifier can be used with any event kind except class unload, thread start, and thread end.
			 */
			public class ClassOnlyRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 4;
				/**
				 * Required class
				 */
				public long clazz;

				public void encode(ByteBuffer bytes) {
					bytes.add(ClassOnlyRequest.MOD_KIND);
					mReferenceTypeID.encode(clazz, bytes);
				}
			}

			/**
			 * Restricts reported events to those for classes whose name matches the given restricted regular
			 * expression. For class prepare events, the prepared class name is matched. For class unload
			 * events, the unloaded class name is matched. For monitor wait and waited events, the name of the
			 * class of the monitor object is matched. For other events, the class name of the event's location
			 * is matched. This modifier can be used with any event kind except thread start and thread end.
			 */
			public class ClassMatchRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 5;
				/**
				 * Required class pattern. Matches are limited to exact matches of the given class pattern and
				 * matches of patterns that begin or end with '*'; for example, "*.Foo" or "java.*".
				 */
				public String classPattern;

				public void encode(ByteBuffer bytes) {
					bytes.add(ClassMatchRequest.MOD_KIND);
					JdwpString.encode(classPattern, bytes);
				}
			}

			/**
			 * Restricts reported events to those for classes whose name does not match the given restricted
			 * regular expression. For class prepare events, the prepared class name is matched. For class
			 * unload events, the unloaded class name is matched. For monitor wait and waited events, the name
			 * of the class of the monitor object is matched. For other events, the class name of the event's
			 * location is matched. This modifier can be used with any event kind except thread start and thread
			 * end.
			 */
			public class ClassExcludeRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 6;
				/**
				 * Disallowed class pattern. Matches are limited to exact matches of the given class pattern and
				 * matches of patterns that begin or end with '*'; for example, "*.Foo" or "java.*".
				 */
				public String classPattern;

				public void encode(ByteBuffer bytes) {
					bytes.add(ClassExcludeRequest.MOD_KIND);
					JdwpString.encode(classPattern, bytes);
				}
			}

			/**
			 * Restricts reported events to those that occur at the given location. This modifier can be used
			 * with breakpoint, field access, field modification, step, and exception event kinds.
			 */
			public class LocationOnlyRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 7;
				/**
				 * Required location
				 */
				public JdwpLocation.LocationPacket loc = new JdwpLocation.LocationPacket();

				public void encode(ByteBuffer bytes) {
					bytes.add(LocationOnlyRequest.MOD_KIND);
					mLocation.encode(loc, bytes);
				}
			}

			/**
			 * Restricts reported exceptions by their class and whether they are caught or uncaught. This
			 * modifier can be used with exception event kinds only.
			 */
			public class ExceptionOnlyRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 8;
				/**
				 * Exception to report. Null (0) means report exceptions of all types. A non-null type restricts the
				 * reported exception events to exceptions of the given type or any of its subtypes.
				 */
				public long exceptionOrNull;
				/**
				 * Report caught exceptions
				 */
				public boolean caught;
				/**
				 * Report uncaught exceptions. Note that it is not always possible to determine whether an exception
				 * is caught or uncaught at the time it is thrown. See the exception event catch location under
				 * composite events for more information.
				 */
				public boolean uncaught;

				public void encode(ByteBuffer bytes) {
					bytes.add(ExceptionOnlyRequest.MOD_KIND);
					mReferenceTypeID.encode(exceptionOrNull, bytes);
					JdwpBoolean.encode(caught, bytes);
					JdwpBoolean.encode(uncaught, bytes);
				}
			}

			/**
			 * Restricts reported events to those that occur for a given field. This modifier can be used with
			 * field access and field modification event kinds only.
			 */
			public class FieldOnlyRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 9;
				/**
				 * Type in which field is declared.
				 */
				public long declaring;
				/**
				 * Required field
				 */
				public long fieldID;

				public void encode(ByteBuffer bytes) {
					bytes.add(FieldOnlyRequest.MOD_KIND);
					mReferenceTypeID.encode(declaring, bytes);
					mFieldID.encode(fieldID, bytes);
				}
			}

			/**
			 * Restricts reported step events to those which satisfy depth and size constraints. This modifier
			 * can be used with step event kinds only.
			 */
			public class StepRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 10;
				/**
				 * Thread in which to step
				 */
				public long thread;
				/**
				 * size of each step. See JDWP.StepSize
				 */
				public int size;
				/**
				 * relative call stack limit. See JDWP.StepDepth
				 */
				public int depth;

				public void encode(ByteBuffer bytes) {
					bytes.add(StepRequest.MOD_KIND);
					mThreadID.encode(thread, bytes);
					JdwpInt.encode(size, bytes);
					JdwpInt.encode(depth, bytes);
				}
			}

			/**
			 * Restricts reported events to those whose active 'this' object is the given object. Match value is
			 * the null object for static methods. This modifier can be used with any event kind except class
			 * prepare, class unload, thread start, and thread end. Introduced in JDWP version 1.4.
			 */
			public class InstanceOnlyRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 11;
				/**
				 * Required 'this' object
				 */
				public long instance;

				public void encode(ByteBuffer bytes) {
					bytes.add(InstanceOnlyRequest.MOD_KIND);
					mObjectID.encode(instance, bytes);
				}
			}

			/**
			 * Restricts reported class prepare events to those for reference types which have a source name
			 * which matches the given restricted regular expression. The source names are determined by the
			 * reference type's SourceDebugExtension. This modifier can only be used with class prepare events.
			 * Since JDWP version 1.6. Requires the canUseSourceNameFilters capability - see CapabilitiesNew.
			 */
			public class SourceNameMatchRequest implements EventRequestEncoder {
				public static final byte MOD_KIND = (byte) 12;
				/**
				 * Required source name pattern. Matches are limited to exact matches of the given pattern and
				 * matches of patterns that begin or end with '*'; for example, "*.Foo" or "java.*".
				 */
				public String sourceNamePattern;

				public void encode(ByteBuffer bytes) {
					bytes.add(SourceNameMatchRequest.MOD_KIND);
					JdwpString.encode(sourceNamePattern, bytes);
				}
			}
		}

		/**
		 * Clear an event request. See JDWP.EventKind for a complete list of events that can be cleared.
		 * Only the event request matching the specified event kind and requestID is cleared. If there isn't
		 * a matching event request the command is a no-op and does not result in an error. Automatically
		 * generated events do not have a corresponding event request and may not be cleared using this
		 * command.
		 */
		public class Clear {

			/**
			 * @param eventKind Event kind to clear
			 * @param requestID ID of request to clear
			 */
			public ByteBuffer encode(byte eventKind, int requestID) {
				ByteBuffer bytes = encodeCommandPacket(15, 2);
				JdwpByte.encode(eventKind, bytes);
				JdwpInt.encode(requestID, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Removes all set breakpoints, a no-op if there are no breakpoints set.
		 */
		public class ClearAllBreakpoints {

			public ByteBuffer encode() {
				ByteBuffer bytes = encodeCommandPacket(15, 3);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}
	}

	public interface EventRequestEncoder {
		void encode(ByteBuffer bytes);
	}

	public class StackFrame {
		private final GetValues cmdGetValues;
		private final SetValues cmdSetValues;
		private final ThisObject cmdThisObject;
		private final PopFrames cmdPopFrames;

		public GetValues cmdGetValues() {
			return cmdGetValues;
		}

		public SetValues cmdSetValues() {
			return cmdSetValues;
		}

		public ThisObject cmdThisObject() {
			return cmdThisObject;
		}

		public PopFrames cmdPopFrames() {
			return cmdPopFrames;
		}

		private StackFrame() {
			cmdGetValues = new GetValues();
			cmdSetValues = new SetValues();
			cmdThisObject = new ThisObject();
			cmdPopFrames = new PopFrames();
		}

		/**
		 * Returns the value of one or more local variables in a given frame. Each variable must be visible
		 * at the frame's code index. Even if local variable information is not available, values can be
		 * retrieved if the front-end is able to determine the correct local variable index. (Typically,
		 * this index can be determined for method arguments from the method signature without access to the
		 * local variable table information.)
		 */
		public class GetValues {

			public class GetValuesSlots {
				/**
				 * The local variable's index in the frame.
				 */
				public int slot;
				/**
				 * A tag identifying the type of the variable
				 */
				public byte sigbyte;
			}

			public GetValuesSlots newValuesSlots() {
				return new GetValuesSlots();
			}

			/**
			 * @param thread The frame's thread.
			 * @param frame  The frame ID.
			 * @param slots  The number of values to get.
			 */
			public ByteBuffer encode(long thread, long frame, List<GetValuesSlots> slots) {
				ByteBuffer bytes = encodeCommandPacket(16, 1);
				mThreadID.encode(thread, bytes);
				mFrameID.encode(frame, bytes);
				JdwpInt.encode(slots.size(), bytes);
				for (GetValuesSlots getValuesSlots : slots) {
					JdwpInt.encode(getValuesSlots.slot, bytes);
					JdwpByte.encode(getValuesSlots.sigbyte, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public class GetValuesReplyData {
				/**
				 * The number of values retrieved, always equal to slots, the number of values to get.
				 */
				public List<GetValuesReplyDataValues> values;
			}

			public class GetValuesReplyDataValues {
				/**
				 * The value of the local variable.
				 */
				public ValuePacket slotValue;
			}

			public GetValuesReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				GetValuesReplyData getValuesReplyData = new GetValuesReplyData();
				int valuesSize = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				getValuesReplyData.values = new ArrayList<>(valuesSize);
				for (int i = 0; i < valuesSize; i++) {
					GetValuesReplyDataValues getValuesReplyDataValues = new GetValuesReplyDataValues();
					getValuesReplyDataValues.slotValue = mValue.decode(bytes, start);
					start += mValue.getSize(getValuesReplyDataValues.slotValue.tag);
					getValuesReplyData.values.add(getValuesReplyDataValues);
				}
				return getValuesReplyData;
			}
		}

		/**
		 * Sets the value of one or more local variables. Each variable must be visible at the current frame
		 * code index. For primitive values, the value's type must match the variable's type exactly. For
		 * object values, there must be a widening reference conversion from the value's type to the
		 * variable's type and the variable's type must be loaded.
		 */
		public class SetValues {

			public class SlotValueSetter {
				/**
				 * The slot ID.
				 */
				public int slot;
				/**
				 * The value to set.
				 */
				public ValuePacket slotValue;
			}

			/**
			 * @param thread     The frame's thread.
			 * @param frame      The frame ID.
			 * @param slotValues The number of values to set.
			 */
			public ByteBuffer encode(long thread, long frame, List<SlotValueSetter> slotValues) throws JdwpRuntimeException {
				ByteBuffer bytes = encodeCommandPacket(16, 2);
				mThreadID.encode(thread, bytes);
				mFrameID.encode(frame, bytes);
				JdwpInt.encode(slotValues.size(), bytes);
				for (SlotValueSetter setValuesSlotValues : slotValues) {
					JdwpInt.encode(setValuesSlotValues.slot, bytes);
					mValue.encode(setValuesSlotValues.slotValue, bytes);
				}
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}

		/**
		 * Returns the value of the 'this' reference for this frame. If the frame's method is static or
		 * native, the reply will contain the null object reference.
		 */
		public class ThisObject {

			/**
			 * @param thread The frame's thread.
			 * @param frame  The frame ID.
			 */
			public ByteBuffer encode(long thread, long frame) {
				ByteBuffer bytes = encodeCommandPacket(16, 3);
				mThreadID.encode(thread, bytes);
				mFrameID.encode(frame, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ThisObjectReplyData {
				/**
				 * The 'this' object for this frame.
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket objectThis;
			}

			public ThisObjectReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ThisObjectReplyData thisObjectReplyData = new ThisObjectReplyData();
				thisObjectReplyData.objectThis = mTaggedobjectID.decode(bytes, start);
				start += mTaggedobjectID.getSize();
				return thisObjectReplyData;
			}
		}

		/**
		 * Pop the top-most stack frames of the thread stack, up to, and including 'frame'. The thread must
		 * be suspended to perform this command. The top-most stack frames are discarded and the stack frame
		 * previous to 'frame' becomes the current frame. The operand stack is restored -- the argument
		 * values are added back and if the invoke was not invokestatic, objectref is added back as well.
		 * The Java virtual machine program counter is restored to the opcode of the invoke instruction.
		 */
		public class PopFrames {

			/**
			 * @param thread The thread object ID.
			 * @param frame  The frame ID.
			 */
			public ByteBuffer encode(long thread, long frame) {
				ByteBuffer bytes = encodeCommandPacket(16, 4);
				mThreadID.encode(thread, bytes);
				mFrameID.encode(frame, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
				return bytes.length == PACKET_HEADER_SIZE;
			}
		}
	}

	public class ClassObjectReference {
		private final ReflectedType cmdReflectedType;

		public ReflectedType cmdReflectedType() {
			return cmdReflectedType;
		}

		private ClassObjectReference() {
			cmdReflectedType = new ReflectedType();
		}

		/**
		 * Returns the reference type reflected by this class object.
		 */
		public class ReflectedType {

			/**
			 * @param classObject The class object.
			 */
			public ByteBuffer encode(long classObject) {
				ByteBuffer bytes = encodeCommandPacket(17, 1);
				mClassObjectID.encode(classObject, bytes);
				setPacketLen(bytes);
				return bytes;
			}

			public class ReflectedTypeReplyData {
				/**
				 * Kind of following reference type.
				 */
				public byte refTypeTag;
				/**
				 * reflected reference type
				 */
				public long typeID;
			}

			public ReflectedTypeReplyData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				ReflectedTypeReplyData reflectedTypeReplyData = new ReflectedTypeReplyData();
				reflectedTypeReplyData.refTypeTag = JdwpByte.decode(bytes, start);
				start += JdwpByte.getSize();
				reflectedTypeReplyData.typeID = mReferenceTypeID.decode(bytes, start);
				start += mReferenceTypeID.getSize();
				return reflectedTypeReplyData;
			}
		}
	}

	public class Event {
		private final Composite cmdComposite;

		public Composite cmdComposite() {
			return cmdComposite;
		}

		private Event() {
			cmdComposite = new Composite();
		}

		/**
		 * The events that are grouped in a composite event are restricted in the following ways:
		 * Only with other thread start events for the same thread:
		 * Thread Start Event
		 * Only with other thread death events for the same thread:
		 * Thread Death Event
		 * Only with other class prepare events for the same class:
		 * Class Prepare Event
		 * Only with other class unload events for the same class:
		 * Class Unload Event
		 * Only with other access watchpoint events for the same field access:
		 * Access Watchpoint Event
		 * Only with other modification watchpoint events for the same field modification:
		 * Modification Watchpoint Event
		 * Only with other Monitor contended enter events for the same monitor object:
		 * Monitor Contended Enter Event
		 * Only with other Monitor contended entered events for the same monitor object:
		 * Monitor Contended Entered Event
		 * Only with other Monitor wait events for the same monitor object:
		 * Monitor Wait Event
		 * Only with other Monitor waited events for the same monitor object:
		 * Monitor Waited Event
		 * Only with other ExceptionEvents for the same exception occurrance:
		 * ExceptionEvent
		 * Only with other members of this group, at the same location and in the same thread:
		 * Breakpoint Event
		 * Step Event
		 * Method Entry Event
		 * Method Exit Event
		 * The VM Start Event and VM Death Event are automatically generated events. This means they do not
		 * need to be requested using the EventRequest.Set command. The VM Start event signals the
		 * completion of VM initialization. The VM Death event signals the termination of the VM.If there is
		 * a debugger connected at the time when an automatically generated event occurs it is sent from the
		 * target VM. Automatically generated events may also be requested using the EventRequest.Set
		 * command and thus multiple events of the same event kind will be sent from the target VM when an
		 * event occurs.Automatically generated events are sent with the requestID field in the Event Data
		 * set to 0. The value of the suspendPolicy field in the Event Data depends on the event. For the
		 * automatically generated VM Start Event the value of suspendPolicy is not defined and is therefore
		 * implementation or configuration specific. In the Sun implementation, for example, the
		 * suspendPolicy is specified as an option to the JDWP agent at launch-time.The automatically
		 * generated VM Death Event will have the suspendPolicy set to NONE.
		 */
		public class Composite {
			public EventData decode(byte[] bytes, int start) throws JdwpRuntimeException {
				EventData data = new EventData();
				data.suspendPolicy = JdwpByte.decode(bytes, start);
				start += JdwpByte.getSize();
				int events = JdwpInt.decode(bytes, start);
				start += JdwpInt.getSize();
				data.events = new ArrayList<>(events);
				int[] starts = new int[] { 0 };
				for (int i = 0; i < events; i++) {
					byte eventKind = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					starts[0] = start;
					data.events.add(getDecoder(eventKind).decode(bytes, starts));
					start = starts[0];
				}
				return data;
			}

			private EventRequestDecoder getDecoder(int eventKind) throws JdwpRuntimeException {
				switch (eventKind) {
					case VMStartEvent.EVENT_KIND:
						return new VMStartEvent();
					case SingleStepEvent.EVENT_KIND:
						return new SingleStepEvent();
					case BreakpointEvent.EVENT_KIND:
						return new BreakpointEvent();
					case MethodEntryEvent.EVENT_KIND:
						return new MethodEntryEvent();
					case MethodExitEvent.EVENT_KIND:
						return new MethodExitEvent();
					case MethodExitWithReturnValueEvent.EVENT_KIND:
						return new MethodExitWithReturnValueEvent();
					case MonitorContendedEnterEvent.EVENT_KIND:
						return new MonitorContendedEnterEvent();
					case MonitorContendedEnteredEvent.EVENT_KIND:
						return new MonitorContendedEnteredEvent();
					case MonitorWaitEvent.EVENT_KIND:
						return new MonitorWaitEvent();
					case MonitorWaitedEvent.EVENT_KIND:
						return new MonitorWaitedEvent();
					case ExceptionEvent.EVENT_KIND:
						return new ExceptionEvent();
					case ThreadStartEvent.EVENT_KIND:
						return new ThreadStartEvent();
					case ThreadDeathEvent.EVENT_KIND:
						return new ThreadDeathEvent();
					case ClassPrepareEvent.EVENT_KIND:
						return new ClassPrepareEvent();
					case ClassUnloadEvent.EVENT_KIND:
						return new ClassUnloadEvent();
					case FieldAccessEvent.EVENT_KIND:
						return new FieldAccessEvent();
					case FieldModificationEvent.EVENT_KIND:
						return new FieldModificationEvent();
					case VMDeathEvent.EVENT_KIND:
						return new VMDeathEvent();
				}
				throw new JdwpRuntimeException("Unexpected event kind: " + eventKind);
			}

			public class EventData {
				/**
				 * Which threads where suspended by this composite event?
				 */
				public byte suspendPolicy;
				/**
				 * Events in set.
				 */
				public List<EventRequestDecoder> events;
			}

			public class VMStartEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.VM_START;
				/**
				 * Request that generated event (or 0 if this event is automatically generated.
				 */
				public int requestID;
				/**
				 * Initial thread
				 */
				public long thread;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					VMStartEvent vMStartEvent = new VMStartEvent();
					vMStartEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					vMStartEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					starts[0] = start;
					return vMStartEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class SingleStepEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.SINGLE_STEP;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Stepped thread
				 */
				public long thread;
				/**
				 * Location stepped to
				 */
				public JdwpLocation.LocationPacket location;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					SingleStepEvent singleStepEvent = new SingleStepEvent();
					singleStepEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					singleStepEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					singleStepEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					starts[0] = start;
					return singleStepEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class BreakpointEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.BREAKPOINT;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which hit breakpoint
				 */
				public long thread;
				/**
				 * Location hit
				 */
				public JdwpLocation.LocationPacket location;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					BreakpointEvent breakpointEvent = new BreakpointEvent();
					breakpointEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					breakpointEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					breakpointEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					starts[0] = start;
					return breakpointEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class MethodEntryEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.METHOD_ENTRY;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which entered method
				 */
				public long thread;
				/**
				 * The initial executable location in the method.
				 */
				public JdwpLocation.LocationPacket location;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					MethodEntryEvent methodEntryEvent = new MethodEntryEvent();
					methodEntryEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					methodEntryEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					methodEntryEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					starts[0] = start;
					return methodEntryEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class MethodExitEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.METHOD_EXIT;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which exited method
				 */
				public long thread;
				/**
				 * Location of exit
				 */
				public JdwpLocation.LocationPacket location;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					MethodExitEvent methodExitEvent = new MethodExitEvent();
					methodExitEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					methodExitEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					methodExitEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					starts[0] = start;
					return methodExitEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class MethodExitWithReturnValueEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.METHOD_EXIT_WITH_RETURN_VALUE;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which exited method
				 */
				public long thread;
				/**
				 * Location of exit
				 */
				public JdwpLocation.LocationPacket location;
				/**
				 * Value that will be returned by the method
				 */
				public ValuePacket value;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					MethodExitWithReturnValueEvent methodExitWithReturnValueEvent = new MethodExitWithReturnValueEvent();
					methodExitWithReturnValueEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					methodExitWithReturnValueEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					methodExitWithReturnValueEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					methodExitWithReturnValueEvent.value = mValue.decode(bytes, start);
					start += mValue.getSize(methodExitWithReturnValueEvent.value.tag);
					starts[0] = start;
					return methodExitWithReturnValueEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class MonitorContendedEnterEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.MONITOR_CONTENDED_ENTER;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which is trying to enter the monitor
				 */
				public long thread;
				/**
				 * Monitor object reference
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket object;
				/**
				 * Location of contended monitor enter
				 */
				public JdwpLocation.LocationPacket location;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					MonitorContendedEnterEvent monitorContendedEnterEvent = new MonitorContendedEnterEvent();
					monitorContendedEnterEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					monitorContendedEnterEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					monitorContendedEnterEvent.object = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					monitorContendedEnterEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					starts[0] = start;
					return monitorContendedEnterEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class MonitorContendedEnteredEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.MONITOR_CONTENDED_ENTERED;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which entered monitor
				 */
				public long thread;
				/**
				 * Monitor object reference
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket object;
				/**
				 * Location of contended monitor enter
				 */
				public JdwpLocation.LocationPacket location;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					MonitorContendedEnteredEvent monitorContendedEnteredEvent = new MonitorContendedEnteredEvent();
					monitorContendedEnteredEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					monitorContendedEnteredEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					monitorContendedEnteredEvent.object = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					monitorContendedEnteredEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					starts[0] = start;
					return monitorContendedEnteredEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class MonitorWaitEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.MONITOR_WAIT;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which is about to wait
				 */
				public long thread;
				/**
				 * Monitor object reference
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket object;
				/**
				 * Location at which the wait will occur
				 */
				public JdwpLocation.LocationPacket location;
				/**
				 * Thread wait time in milliseconds
				 */
				public long timeout;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					MonitorWaitEvent monitorWaitEvent = new MonitorWaitEvent();
					monitorWaitEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					monitorWaitEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					monitorWaitEvent.object = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					monitorWaitEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					monitorWaitEvent.timeout = JdwpLong.decode(bytes, start);
					start += JdwpLong.getSize();
					starts[0] = start;
					return monitorWaitEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class MonitorWaitedEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.MONITOR_WAITED;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread which waited
				 */
				public long thread;
				/**
				 * Monitor object reference
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket object;
				/**
				 * Location at which the wait occured
				 */
				public JdwpLocation.LocationPacket location;
				/**
				 * True if timed out
				 */
				public boolean timedOut;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					MonitorWaitedEvent monitorWaitedEvent = new MonitorWaitedEvent();
					monitorWaitedEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					monitorWaitedEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					monitorWaitedEvent.object = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					monitorWaitedEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					monitorWaitedEvent.timedOut = JdwpBoolean.decode(bytes, start);
					start += JdwpBoolean.getSize();
					starts[0] = start;
					return monitorWaitedEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class ExceptionEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.EXCEPTION;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Thread with exception
				 */
				public long thread;
				/**
				 * Location of exception throw (or first non-native location after throw if thrown from a native
				 * method)
				 */
				public JdwpLocation.LocationPacket location;
				/**
				 * Thrown exception
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket exception;
				/**
				 * Location of catch, or 0 if not caught. An exception is considered to be caught if, at the point
				 * of the throw, the current location is dynamically enclosed in a try statement that handles the
				 * exception. (See the JVM specification for details). If there is such a try statement, the catch
				 * location is the first location in the appropriate catch clause.
				 */
				public JdwpLocation.LocationPacket catchLocation;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					ExceptionEvent exceptionEvent = new ExceptionEvent();
					exceptionEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					exceptionEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					exceptionEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					exceptionEvent.exception = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					exceptionEvent.catchLocation = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					starts[0] = start;
					return exceptionEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class ThreadStartEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.THREAD_START;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Started thread
				 */
				public long thread;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					ThreadStartEvent threadStartEvent = new ThreadStartEvent();
					threadStartEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					threadStartEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					starts[0] = start;
					return threadStartEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class ThreadDeathEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.THREAD_DEATH;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Ending thread
				 */
				public long thread;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					ThreadDeathEvent threadDeathEvent = new ThreadDeathEvent();
					threadDeathEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					threadDeathEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					starts[0] = start;
					return threadDeathEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class ClassPrepareEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.CLASS_PREPARE;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Preparing thread. In rare cases, this event may occur in a debugger system thread within the
				 * target VM. Debugger threads take precautions to prevent these events, but they cannot be avoided
				 * under some conditions, especially for some subclasses of java.lang.Error. If the event was
				 * generated by a debugger system thread, the value returned by this method is null, and if the
				 * requested suspend policy for the event was EVENT_THREAD all threads will be suspended instead,
				 * and the composite event's suspend policy will reflect this change.
				 */
				public long thread;
				/**
				 * Kind of reference type. See JDWP.TypeTag
				 */
				public byte refTypeTag;
				/**
				 * Type being prepared
				 */
				public long typeID;
				/**
				 * Type signature
				 */
				public String signature;
				/**
				 * Status of type. See JDWP.ClassStatus
				 */
				public int status;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					ClassPrepareEvent classPrepareEvent = new ClassPrepareEvent();
					classPrepareEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					classPrepareEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					classPrepareEvent.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					classPrepareEvent.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					classPrepareEvent.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(classPrepareEvent.signature);
					classPrepareEvent.status = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					starts[0] = start;
					return classPrepareEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class ClassUnloadEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.CLASS_UNLOAD;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Type signature
				 */
				public String signature;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					ClassUnloadEvent classUnloadEvent = new ClassUnloadEvent();
					classUnloadEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					classUnloadEvent.signature = JdwpString.decode(bytes, start);
					start += JdwpString.getSize(classUnloadEvent.signature);
					starts[0] = start;
					return classUnloadEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class FieldAccessEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.FIELD_ACCESS;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Accessing thread
				 */
				public long thread;
				/**
				 * Location of access
				 */
				public JdwpLocation.LocationPacket location;
				/**
				 * Kind of reference type. See JDWP.TypeTag
				 */
				public byte refTypeTag;
				/**
				 * Type of field
				 */
				public long typeID;
				/**
				 * Field being accessed
				 */
				public long fieldID;
				/**
				 * Object being accessed (null=0 for statics
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket object;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					FieldAccessEvent fieldAccessEvent = new FieldAccessEvent();
					fieldAccessEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					fieldAccessEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					fieldAccessEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					fieldAccessEvent.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					fieldAccessEvent.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					fieldAccessEvent.fieldID = mFieldID.decode(bytes, start);
					start += mFieldID.getSize();
					fieldAccessEvent.object = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					starts[0] = start;
					return fieldAccessEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class FieldModificationEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.FIELD_MODIFICATION;
				/**
				 * Request that generated event
				 */
				public int requestID;
				/**
				 * Modifying thread
				 */
				public long thread;
				/**
				 * Location of modify
				 */
				public JdwpLocation.LocationPacket location;
				/**
				 * Kind of reference type. See JDWP.TypeTag
				 */
				public byte refTypeTag;
				/**
				 * Type of field
				 */
				public long typeID;
				/**
				 * Field being modified
				 */
				public long fieldID;
				/**
				 * Object being modified (null=0 for statics
				 */
				public JdwpTaggedobjectID.TaggedObjectIDPacket object;
				/**
				 * Value to be assigned
				 */
				public ValuePacket valueToBe;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					FieldModificationEvent fieldModificationEvent = new FieldModificationEvent();
					fieldModificationEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					fieldModificationEvent.thread = mThreadID.decode(bytes, start);
					start += mThreadID.getSize();
					fieldModificationEvent.location = mLocation.decode(bytes, start);
					start += mLocation.getSize();
					fieldModificationEvent.refTypeTag = JdwpByte.decode(bytes, start);
					start += JdwpByte.getSize();
					fieldModificationEvent.typeID = mReferenceTypeID.decode(bytes, start);
					start += mReferenceTypeID.getSize();
					fieldModificationEvent.fieldID = mFieldID.decode(bytes, start);
					start += mFieldID.getSize();
					fieldModificationEvent.object = mTaggedobjectID.decode(bytes, start);
					start += mTaggedobjectID.getSize();
					fieldModificationEvent.valueToBe = mValue.decode(bytes, start);
					start += mValue.getSize(fieldModificationEvent.valueToBe.tag);
					starts[0] = start;
					return fieldModificationEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}

			public class VMDeathEvent implements EventRequestDecoder {
				public static final byte EVENT_KIND = JDWP.EventKind.VM_DEATH;
				/**
				 * Request that generated event
				 */
				public int requestID;

				@Override
				public EventRequestDecoder decode(byte[] bytes, int[] starts) throws JdwpRuntimeException {
					int start = starts[0];
					VMDeathEvent vMDeathEvent = new VMDeathEvent();
					vMDeathEvent.requestID = JdwpInt.decode(bytes, start);
					start += JdwpInt.getSize();
					starts[0] = start;
					return vMDeathEvent;
				}

				@Override
				public int getRequestID() {
					return requestID;
				}
			}
		}
	}

	public interface EventRequestDecoder {
		EventRequestDecoder decode(byte[] bytes, int[] start) throws JdwpRuntimeException;

		int getRequestID();
	}

	public static class Error {
		/**
		 * No error has occurred.
		 */
		public static final int NONE = 0;
		/**
		 * Passed thread is null, is not a valid thread or has exited.
		 */
		public static final int INVALID_THREAD = 10;
		/**
		 * Thread group invalid.
		 */
		public static final int INVALID_THREAD_GROUP = 11;
		/**
		 * Invalid priority.
		 */
		public static final int INVALID_PRIORITY = 12;
		/**
		 * If the specified thread has not been suspended by an event.
		 */
		public static final int THREAD_NOT_SUSPENDED = 13;
		/**
		 * Thread already suspended.
		 */
		public static final int THREAD_SUSPENDED = 14;
		/**
		 * Thread has not been started or is now dead.
		 */
		public static final int THREAD_NOT_ALIVE = 15;
		/**
		 * If this reference type has been unloaded and garbage collected.
		 */
		public static final int INVALID_OBJECT = 20;
		/**
		 * Invalid class.
		 */
		public static final int INVALID_CLASS = 21;
		/**
		 * Class has been loaded but not yet prepared.
		 */
		public static final int CLASS_NOT_PREPARED = 22;
		/**
		 * Invalid method.
		 */
		public static final int INVALID_METHODID = 23;
		/**
		 * Invalid location.
		 */
		public static final int INVALID_LOCATION = 24;
		/**
		 * Invalid field.
		 */
		public static final int INVALID_FIELDID = 25;
		/**
		 * Invalid jframeID.
		 */
		public static final int INVALID_FRAMEID = 30;
		/**
		 * There are no more Java or JNI frames on the call stack.
		 */
		public static final int NO_MORE_FRAMES = 31;
		/**
		 * Information about the frame is not available.
		 */
		public static final int OPAQUE_FRAME = 32;
		/**
		 * Operation can only be performed on current frame.
		 */
		public static final int NOT_CURRENT_FRAME = 33;
		/**
		 * The variable is not an appropriate type for the function used.
		 */
		public static final int TYPE_MISMATCH = 34;
		/**
		 * Invalid slot.
		 */
		public static final int INVALID_SLOT = 35;
		/**
		 * Item already set.
		 */
		public static final int DUPLICATE = 40;
		/**
		 * Desired element not found.
		 */
		public static final int NOT_FOUND = 41;
		/**
		 * Invalid monitor.
		 */
		public static final int INVALID_MONITOR = 50;
		/**
		 * This thread doesn't own the monitor.
		 */
		public static final int NOT_MONITOR_OWNER = 51;
		/**
		 * The call has been interrupted before completion.
		 */
		public static final int INTERRUPT = 52;
		/**
		 * The virtual machine attempted to read a class file and determined that the file is malformed or
		 * otherwise cannot be interpreted as a class file.
		 */
		public static final int INVALID_CLASS_FORMAT = 60;
		/**
		 * A circularity has been detected while initializing a class.
		 */
		public static final int CIRCULAR_CLASS_DEFINITION = 61;
		/**
		 * The verifier detected that a class file, though well formed, contained some sort of internal
		 * inconsistency or security problem.
		 */
		public static final int FAILS_VERIFICATION = 62;
		/**
		 * Adding methods has not been implemented.
		 */
		public static final int ADD_METHOD_NOT_IMPLEMENTED = 63;
		/**
		 * Schema change has not been implemented.
		 */
		public static final int SCHEMA_CHANGE_NOT_IMPLEMENTED = 64;
		/**
		 * The state of the thread has been modified, and is now inconsistent.
		 */
		public static final int INVALID_TYPESTATE = 65;
		/**
		 * A direct superclass is different for the new class version, or the set of directly implemented
		 * interfaces is different and canUnrestrictedlyRedefineClasses is false.
		 */
		public static final int HIERARCHY_CHANGE_NOT_IMPLEMENTED = 66;
		/**
		 * The new class version does not declare a method declared in the old class version and
		 * canUnrestrictedlyRedefineClasses is false.
		 */
		public static final int DELETE_METHOD_NOT_IMPLEMENTED = 67;
		/**
		 * A class file has a version number not supported by this VM.
		 */
		public static final int UNSUPPORTED_VERSION = 68;
		/**
		 * The class name defined in the new class file is different from the name in the old class object.
		 */
		public static final int NAMES_DONT_MATCH = 69;
		/**
		 * The new class version has different modifiers and and canUnrestrictedlyRedefineClasses is false.
		 */
		public static final int CLASS_MODIFIERS_CHANGE_NOT_IMPLEMENTED = 70;
		/**
		 * A method in the new class version has different modifiers than its counterpart in the old class
		 * version and and canUnrestrictedlyRedefineClasses is false.
		 */
		public static final int METHOD_MODIFIERS_CHANGE_NOT_IMPLEMENTED = 71;
		/**
		 * The functionality is not implemented in this virtual machine.
		 */
		public static final int NOT_IMPLEMENTED = 99;
		/**
		 * Invalid pointer.
		 */
		public static final int NULL_POINTER = 100;
		/**
		 * Desired information is not available.
		 */
		public static final int ABSENT_INFORMATION = 101;
		/**
		 * The specified event type id is not recognized.
		 */
		public static final int INVALID_EVENT_TYPE = 102;
		/**
		 * Illegal argument.
		 */
		public static final int ILLEGAL_ARGUMENT = 103;
		/**
		 * The function needed to allocate memory and no more memory was available for allocation.
		 */
		public static final int OUT_OF_MEMORY = 110;
		/**
		 * Debugging has not been enabled in this virtual machine. JVMTI cannot be used.
		 */
		public static final int ACCESS_DENIED = 111;
		/**
		 * The virtual machine is not running.
		 */
		public static final int VM_DEAD = 112;
		/**
		 * An unexpected internal error has occurred.
		 */
		public static final int INTERNAL = 113;
		/**
		 * The thread being used to call this function is not attached to the virtual machine. Calls must be
		 * made from attached threads.
		 */
		public static final int UNATTACHED_THREAD = 115;
		/**
		 * object type id or class tag.
		 */
		public static final int INVALID_TAG = 500;
		/**
		 * Previous invoke not complete.
		 */
		public static final int ALREADY_INVOKING = 502;
		/**
		 * Index is invalid.
		 */
		public static final int INVALID_INDEX = 503;
		/**
		 * The length is invalid.
		 */
		public static final int INVALID_LENGTH = 504;
		/**
		 * The string is invalid.
		 */
		public static final int INVALID_STRING = 506;
		/**
		 * The class loader is invalid.
		 */
		public static final int INVALID_CLASS_LOADER = 507;
		/**
		 * The array is invalid.
		 */
		public static final int INVALID_ARRAY = 508;
		/**
		 * Unable to load the transport.
		 */
		public static final int TRANSPORT_LOAD = 509;
		/**
		 * Unable to initialize the transport.
		 */
		public static final int TRANSPORT_INIT = 510;
		public static final int NATIVE_METHOD = 511;
		/**
		 * The count is invalid.
		 */
		public static final int INVALID_COUNT = 512;

		public static String getErrorText(int errCode) {
			switch (errCode) {
				case NONE:
					return "No error has occurred.";
				case INVALID_THREAD:
					return "Passed thread is null, is not a valid thread or has exited.";
				case INVALID_THREAD_GROUP:
					return "Thread group invalid.";
				case INVALID_PRIORITY:
					return "Invalid priority.";
				case THREAD_NOT_SUSPENDED:
					return "If the specified thread has not been suspended by an event.";
				case THREAD_SUSPENDED:
					return "Thread already suspended.";
				case THREAD_NOT_ALIVE:
					return "Thread has not been started or is now dead.";
				case INVALID_OBJECT:
					return "If this reference type has been unloaded and garbage collected.";
				case INVALID_CLASS:
					return "Invalid class.";
				case CLASS_NOT_PREPARED:
					return "Class has been loaded but not yet prepared.";
				case INVALID_METHODID:
					return "Invalid method.";
				case INVALID_LOCATION:
					return "Invalid location.";
				case INVALID_FIELDID:
					return "Invalid field.";
				case INVALID_FRAMEID:
					return "Invalid jframeID.";
				case NO_MORE_FRAMES:
					return "There are no more Java or JNI frames on the call stack.";
				case OPAQUE_FRAME:
					return "Information about the frame is not available.";
				case NOT_CURRENT_FRAME:
					return "Operation can only be performed on current frame.";
				case TYPE_MISMATCH:
					return "The variable is not an appropriate type for the function used.";
				case INVALID_SLOT:
					return "Invalid slot.";
				case DUPLICATE:
					return "Item already set.";
				case NOT_FOUND:
					return "Desired element not found.";
				case INVALID_MONITOR:
					return "Invalid monitor.";
				case NOT_MONITOR_OWNER:
					return "This thread doesn't own the monitor.";
				case INTERRUPT:
					return "The call has been interrupted before completion.";
				case INVALID_CLASS_FORMAT:
					return "The virtual machine attempted to read a class file and determined that the file is malformed or otherwise cannot be interpreted as a class file.";
				case CIRCULAR_CLASS_DEFINITION:
					return "A circularity has been detected while initializing a class.";
				case FAILS_VERIFICATION:
					return "The verifier detected that a class file, though well formed, contained some sort of internal inconsistency or security problem.";
				case ADD_METHOD_NOT_IMPLEMENTED:
					return "Adding methods has not been implemented.";
				case SCHEMA_CHANGE_NOT_IMPLEMENTED:
					return "Schema change has not been implemented.";
				case INVALID_TYPESTATE:
					return "The state of the thread has been modified, and is now inconsistent.";
				case HIERARCHY_CHANGE_NOT_IMPLEMENTED:
					return "A direct superclass is different for the new class version, or the set of directly implemented interfaces is different and canUnrestrictedlyRedefineClasses is false.";
				case DELETE_METHOD_NOT_IMPLEMENTED:
					return "The new class version does not declare a method declared in the old class version and canUnrestrictedlyRedefineClasses is false.";
				case UNSUPPORTED_VERSION:
					return "A class file has a version number not supported by this VM.";
				case NAMES_DONT_MATCH:
					return "The class name defined in the new class file is different from the name in the old class object.";
				case CLASS_MODIFIERS_CHANGE_NOT_IMPLEMENTED:
					return "The new class version has different modifiers and and canUnrestrictedlyRedefineClasses is false.";
				case METHOD_MODIFIERS_CHANGE_NOT_IMPLEMENTED:
					return "A method in the new class version has different modifiers than its counterpart in the old class version and and canUnrestrictedlyRedefineClasses is false.";
				case NOT_IMPLEMENTED:
					return "The functionality is not implemented in this virtual machine.";
				case NULL_POINTER:
					return "Invalid pointer.";
				case ABSENT_INFORMATION:
					return "Desired information is not available.";
				case INVALID_EVENT_TYPE:
					return "The specified event type id is not recognized.";
				case ILLEGAL_ARGUMENT:
					return "Illegal argument.";
				case OUT_OF_MEMORY:
					return "The function needed to allocate memory and no more memory was available for allocation.";
				case ACCESS_DENIED:
					return "Debugging has not been enabled in this virtual machine. JVMTI cannot be used.";
				case VM_DEAD:
					return "The virtual machine is not running.";
				case INTERNAL:
					return "An unexpected internal error has occurred.";
				case UNATTACHED_THREAD:
					return "The thread being used to call this function is not attached to the virtual machine. Calls must be made from attached threads.";
				case INVALID_TAG:
					return "object type id or class tag.";
				case ALREADY_INVOKING:
					return "Previous invoke not complete.";
				case INVALID_INDEX:
					return "Index is invalid.";
				case INVALID_LENGTH:
					return "The length is invalid.";
				case INVALID_STRING:
					return "The string is invalid.";
				case INVALID_CLASS_LOADER:
					return "The class loader is invalid.";
				case INVALID_ARRAY:
					return "The array is invalid.";
				case TRANSPORT_LOAD:
					return "Unable to load the transport.";
				case TRANSPORT_INIT:
					return "Unable to initialize the transport.";
				case NATIVE_METHOD:
					return "NATIVE_METHOD";
				case INVALID_COUNT:
					return "The count is invalid.";
			}
			return "Unknown error.";
		}
	}

	public static class EventKind {
		public static final int SINGLE_STEP = 1;
		public static final int BREAKPOINT = 2;
		public static final int FRAME_POP = 3;
		public static final int EXCEPTION = 4;
		public static final int USER_DEFINED = 5;
		public static final int THREAD_START = 6;
		public static final int THREAD_DEATH = 7;
		/**
		 * obsolete - was used in jvmdi
		 */
		public static final int THREAD_END = 7;
		public static final int CLASS_PREPARE = 8;
		public static final int CLASS_UNLOAD = 9;
		public static final int CLASS_LOAD = 10;
		public static final int FIELD_ACCESS = 20;
		public static final int FIELD_MODIFICATION = 21;
		public static final int EXCEPTION_CATCH = 30;
		public static final int METHOD_ENTRY = 40;
		public static final int METHOD_EXIT = 41;
		public static final int METHOD_EXIT_WITH_RETURN_VALUE = 42;
		public static final int MONITOR_CONTENDED_ENTER = 43;
		public static final int MONITOR_CONTENDED_ENTERED = 44;
		public static final int MONITOR_WAIT = 45;
		public static final int MONITOR_WAITED = 46;
		public static final int VM_START = 90;
		/**
		 * obsolete - was used in jvmdi
		 */
		public static final int VM_INIT = 90;
		public static final int VM_DEATH = 99;
		/**
		 * Never sent across JDWP
		 */
		public static final int VM_DISCONNECTED = 100;
	}

	public static class ThreadStatus {
		public static final int ZOMBIE = 0;
		public static final int RUNNING = 1;
		public static final int SLEEPING = 2;
		public static final int MONITOR = 3;
		public static final int WAIT = 4;
	}

	public static class SuspendStatus {
		public static final int SUSPEND_STATUS_SUSPENDED = 0x1;
	}

	public static class ClassStatus {
		public static final int VERIFIED = 1;
		public static final int PREPARED = 2;
		public static final int INITIALIZED = 4;
		public static final int ERROR = 8;
	}

	public static class TypeTag {
		/**
		 * ReferenceType is a class.
		 */
		public static final int CLASS = 1;
		/**
		 * ReferenceType is an interface.
		 */
		public static final int INTERFACE = 2;
		/**
		 * ReferenceType is an array.
		 */
		public static final int ARRAY = 3;
	}

	public static class Tag {
		/**
		 * '[' - an array object (objectID size).
		 */
		public static final int ARRAY = 91;
		/**
		 * 'B' - a byte value (1 byte).
		 */
		public static final int BYTE = 66;
		/**
		 * 'C' - a character value (2 bytes).
		 */
		public static final int CHAR = 67;
		/**
		 * 'L' - an object (objectID size).
		 */
		public static final int OBJECT = 76;
		/**
		 * 'F' - a float value (4 bytes).
		 */
		public static final int FLOAT = 70;
		/**
		 * 'D' - a double value (8 bytes).
		 */
		public static final int DOUBLE = 68;
		/**
		 * 'I' - an int value (4 bytes).
		 */
		public static final int INT = 73;
		/**
		 * 'J' - a long value (8 bytes).
		 */
		public static final int LONG = 74;
		/**
		 * 'S' - a short value (2 bytes).
		 */
		public static final int SHORT = 83;
		/**
		 * 'V' - a void value (no bytes).
		 */
		public static final int VOID = 86;
		/**
		 * 'Z' - a boolean value (1 byte).
		 */
		public static final int BOOLEAN = 90;
		/**
		 * 's' - a String object (objectID size).
		 */
		public static final int STRING = 115;
		/**
		 * 't' - a Thread object (objectID size).
		 */
		public static final int THREAD = 116;
		/**
		 * 'g' - a ThreadGroup object (objectID size).
		 */
		public static final int THREAD_GROUP = 103;
		/**
		 * 'l' - a ClassLoader object (objectID size).
		 */
		public static final int CLASS_LOADER = 108;
		/**
		 * 'c' - a class object object (objectID size).
		 */
		public static final int CLASS_OBJECT = 99;
	}

	public static class StepDepth {
		/**
		 * Step into any method calls that occur before the end of the step.
		 */
		public static final int INTO = 0;
		/**
		 * Step over any method calls that occur before the end of the step.
		 */
		public static final int OVER = 1;
		/**
		 * Step out of the current method.
		 */
		public static final int OUT = 2;
	}

	public static class StepSize {
		/**
		 * Step by the minimum possible amount (often a bytecode instruction).
		 */
		public static final int MIN = 0;
		/**
		 * Step to the next source line unless there is no line number information in which case a MIN step
		 * is done instead.
		 */
		public static final int LINE = 1;
	}

	public static class SuspendPolicy {
		/**
		 * Suspend no threads when this event is encountered.
		 */
		public static final int NONE = 0;
		/**
		 * Suspend the event thread when this event is encountered.
		 */
		public static final int EVENT_THREAD = 1;
		/**
		 * Suspend all threads when this event is encountered.
		 */
		public static final int ALL = 2;
	}

	/**
	 * The invoke options are a combination of zero or more of the following bit flags:
	 */
	public static class InvokeOptions {
		/**
		 * otherwise, all threads started.
		 */
		public static final int INVOKE_SINGLE_THREADED = 0x01;
		/**
		 * otherwise, normal virtual invoke (instance methods only)
		 */
		public static final int INVOKE_NONVIRTUAL = 0x02;
	}

	/**
	 * A byte value.
	 */
	private static class JdwpByte {

		static byte decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeByte(bytes, start);
		}

		static void encode(byte val, ByteBuffer bytes) {
			JDWP.encodeByte(bytes, val);
		}

		static int getSize() {
			return 1;
		}

	}

	/**
	 * A boolean value, encoded as 0 for false and non-zero for true.
	 */
	private static class JdwpBoolean {

		static boolean decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBoolean(bytes, start);
		}

		static void encode(boolean val, ByteBuffer bytes) {
			JDWP.encodeBoolean(bytes, val);
		}

		static int getSize() {
			return 1;
		}

	}

	/**
	 * An four-byte integer value. The integer is signed unless explicitly stated to be unsigned.
	 */
	private static class JdwpInt {

		static int decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeInt(bytes, start);
		}

		static void encode(int val, ByteBuffer bytes) {
			JDWP.encodeInt(bytes, val);
		}

		static int getSize() {
			return 4;
		}

	}

	/**
	 * An eight-byte integer value. The value is signed unless explicitly stated to be unsigned.
	 */
	private static class JdwpLong {

		static long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		static void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		static int getSize() {
			return 8;
		}

	}

	/**
	 * Uniquely identifies an object in the target VM. A particular object will be identified by exactly
	 * one objectID in JDWP commands and replies throughout its lifetime (or until the objectID is
	 * explicitly disposed). An ObjectID is not reused to identify a different object unless it has been
	 * explicitly disposed, regardless of whether the referenced object has been garbage collected. An
	 * objectID of 0 represents a null object. Note that the existence of an object ID does not prevent
	 * the garbage collection of the object. Any attempt to access a a garbage collected object with its
	 * object ID will result in the INVALID_OBJECT error code. Garbage collection can be disabled with
	 * the DisableCollection command, but it is not usually necessary to do so.
	 */
	private static class JdwpObjectID {
		private final int size;

		JdwpObjectID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * The first byte is a signature byte which is used to identify the object's type. See JDWP.Tag for
	 * the possible values of this byte (note that only object tags, not primitive tags, may be used).
	 * It is followed immediately by the objectID itself.
	 */
	private static class JdwpTaggedobjectID {
		private final int size;

		JdwpTaggedobjectID(int objectIDSize) {
			this.size = objectIDSize;
		}

		TaggedObjectIDPacket decode(byte[] bytes, int start) throws JdwpRuntimeException {
			TaggedObjectIDPacket taggedObjectIDPacket = new TaggedObjectIDPacket();
			taggedObjectIDPacket.tag = decodeByte(bytes, start);
			taggedObjectIDPacket.objectID = decodeBySize(bytes, start + 1, size);
			return taggedObjectIDPacket;
		}

		void encode(TaggedObjectIDPacket val, ByteBuffer bytes) {
			JDWP.encodeByte(bytes, (byte) val.tag);
			encodeBySize(bytes, size, val.objectID);
		}

		int getSize() {
			return 1 + size;
		}

		public static class TaggedObjectIDPacket {
			public int tag;
			public long objectID;
		}
	}

	/**
	 * Uniquely identifies an object in the target VM that is known to be a thread.
	 */
	private static class JdwpThreadID {
		private final int size;

		JdwpThreadID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies an object in the target VM that is known to be a thread group.
	 */
	private static class JdwpThreadGroupID {
		private final int size;

		JdwpThreadGroupID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies an object in the target VM that is known to be a string object. Note: this is
	 * very different from string, which is a value.
	 */
	private static class JdwpStringID {
		private final int size;

		JdwpStringID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies an object in the target VM that is known to be a module object.
	 */
	private static class JdwpModuleID {
		private final int size;

		JdwpModuleID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies an object in the target VM that is known to be a class loader object.
	 */
	private static class JdwpClassLoaderID {
		private final int size;

		JdwpClassLoaderID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies an object in the target VM that is known to be a class object.
	 */
	private static class JdwpClassObjectID {
		private final int size;

		JdwpClassObjectID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies an object in the target VM that is known to be an array.
	 */
	private static class JdwpArrayID {
		private final int size;

		JdwpArrayID(int objectIDSize) {
			this.size = objectIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies a reference type in the target VM. It should not be assumed that for a
	 * particular class, the classObjectID and the referenceTypeID are the same. A particular reference
	 * type will be identified by exactly one ID in JDWP commands and replies throughout its lifetime A
	 * referenceTypeID is not reused to identify a different reference type, regardless of whether the
	 * referenced class has been unloaded.
	 */
	private static class JdwpReferenceTypeID {
		private final int size;

		JdwpReferenceTypeID(int referenceTypeIDSize) {
			this.size = referenceTypeIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies a reference type in the target VM that is known to be a class type.
	 */
	private static class JdwpClassID {
		private final int size;

		JdwpClassID(int referenceTypeIDSize) {
			this.size = referenceTypeIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies a reference type in the target VM that is known to be an interface type.
	 */
	private static class JdwpInterfaceID {
		private final int size;

		JdwpInterfaceID(int referenceTypeIDSize) {
			this.size = referenceTypeIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies a reference type in the target VM that is known to be an array type.
	 */
	private static class JdwpArrayTypeID {
		private final int size;

		JdwpArrayTypeID(int referenceTypeIDSize) {
			this.size = referenceTypeIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies a method in some class in the target VM. The methodID must uniquely identify
	 * the method within its class/interface or any of its subclasses/subinterfaces/implementors. A
	 * methodID is not necessarily unique on its own; it is always paired with a referenceTypeID to
	 * uniquely identify one method. The referenceTypeID can identify either the declaring type of the
	 * method or a subtype.
	 */
	private static class JdwpMethodID {
		private final int size;

		JdwpMethodID(int methodIDSize) {
			this.size = methodIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies a field in some class in the target VM. The fieldID must uniquely identify
	 * the field within its class/interface or any of its subclasses/subinterfaces/implementors. A
	 * fieldID is not necessarily unique on its own; it is always paired with a referenceTypeID to
	 * uniquely identify one field. The referenceTypeID can identify either the declaring type of the
	 * field or a subtype.
	 */
	private static class JdwpFieldID {
		private final int size;

		JdwpFieldID(int fieldIDSize) {
			this.size = fieldIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * Uniquely identifies a frame in the target VM. The frameID must uniquely identify the frame within
	 * the entire VM (not only within a given thread). The frameID need only be valid during the time
	 * its thread is suspended.
	 */
	private static class JdwpFrameID {
		private final int size;

		JdwpFrameID(int frameIDSize) {
			this.size = frameIDSize;
		}

		long decode(byte[] bytes, int start) throws JdwpRuntimeException {
			return decodeBySize(bytes, start, getSize());
		}

		void encode(long val, ByteBuffer bytes) {
			encodeBySize(bytes, getSize(), val);
		}

		int getSize() {
			return size;
		}

	}

	/**
	 * An executable location. The location is identified by one byte type tag followed by a a classID
	 * followed by a methodID followed by an unsigned eight-byte index, which identifies the location
	 * within the method. See below for details on the location index. The type tag is necessary to
	 * identify whether location's classID identifies a class or an interface. Almost all locations are
	 * within classes, but it is possible to have executable code in the static initializer of an
	 * interface.
	 */
	private static class JdwpLocation {
		int size;
		int classIDSize;
		int methodIDSize;

		JdwpLocation(int classIDSize, int methodIDSize) {
			this.classIDSize = classIDSize;
			this.methodIDSize = methodIDSize;
			size = classIDSize + methodIDSize;
		}

		LocationPacket decode(byte[] bytes, int start) throws JdwpRuntimeException {
			LocationPacket locationPacket = new LocationPacket();
			locationPacket.tag = decodeByte(bytes, start);
			locationPacket.classID = decodeBySize(bytes, start + 1, classIDSize);
			locationPacket.methodID = decodeBySize(bytes, start + classIDSize + 1, methodIDSize);
			locationPacket.index = decodeBySize(bytes, start + classIDSize + methodIDSize + 1, 8);
			return locationPacket;
		}

		void encode(LocationPacket val, ByteBuffer bytes) {
			JDWP.encodeByte(bytes, (byte) val.tag);
			encodeBySize(bytes, classIDSize, val.classID);
			encodeBySize(bytes, methodIDSize, val.methodID);
			encodeBySize(bytes, 8, val.index);
		}

		int getSize() {
			return 1 + size + 8;
		}

		public static class LocationPacket {
			public int tag;
			public long classID;
			public long methodID;
			public long index;
		}
	}

	/**
	 * A UTF-8 encoded string, not zero terminated, preceded by a four-byte integer length.
	 */
	private static class JdwpString {

		static String decode(byte[] bytes, int start) throws JdwpRuntimeException {
			int len = decodeInt(bytes, start);
			return new String(decodeRaw(bytes, start + 4, len), StandardCharsets.UTF_8);
		}

		static void encode(String val, ByteBuffer bytes) {
			byte[] encoded = val.getBytes(StandardCharsets.UTF_8);
			JDWP.encodeInt(bytes, encoded.length);
			bytes.addAll(encoded);
		}

		static int getSize(String str) {
			return 4 + str.getBytes(StandardCharsets.UTF_8).length;
		}

	}

	/**
	 * A value retrieved from the target VM. The first byte is a signature byte which is used to
	 * identify the type. See JDWP.Tag for the possible values of this byte. It is followed immediately
	 * by the value itself. This value can be an objectID (see Get ID Sizes) or a primitive value (1 to
	 * 8 bytes). More details about each value type can be found in the next table.
	 */
	private class JdwpValue {

		ValuePacket decode(byte[] bytes, int start) throws JdwpRuntimeException {
			ValuePacket valuePacket = new ValuePacket();
			int tag = decodeByte(bytes, start);
			valuePacket.tag = tag;
			int tagSize = getSize(tag) - 1;
			valuePacket.idOrValue = new ByteBuffer(decodeRaw(bytes, start + 1, tagSize));
			return valuePacket;
		}

		void encode(ValuePacket val, ByteBuffer bytes) throws JdwpRuntimeException {
			JDWP.encodeByte(bytes, (byte) val.tag);
			encodeRaw(bytes, val.idOrValue);
		}

		int getSize(int tag) throws JdwpRuntimeException {
			return 1 + valueTag.getSize(tag);
		}
	}

	public class ValuePacket extends ValueTag {
	}

	/**
	 * A value as described above without the signature byte. This form is used when the signature
	 * information can be determined from context.
	 */
	private class JdwpUntaggedvalue {

		UntaggedValuePacket decode(byte[] bytes, int start, int tag) throws JdwpRuntimeException {
			UntaggedValuePacket untaggedValuePacket = new UntaggedValuePacket();
			untaggedValuePacket.tag = tag;
			untaggedValuePacket.idOrValue = new ByteBuffer(decodeRaw(bytes, start, getSize(tag)));
			return untaggedValuePacket;
		}

		void encode(UntaggedValuePacket val, ByteBuffer bytes) {
			encodeRaw(bytes, val.idOrValue);
		}

		int getSize(int tag) throws JdwpRuntimeException {
			return valueTag.getSize(tag);
		}
	}

	public class UntaggedValuePacket extends ValueTag {
	}

	/**
	 * A compact representation of values used with some array operations. The first byte is a signature
	 * byte which is used to identify the type. See JDWP.Tag for the possible values of this byte. Next
	 * is a four-byte integer indicating the number of values in the sequence. This is followed by the
	 * values themselves: Primitive values are encoded as a sequence of untagged-values; Object values
	 * are encoded as a sequence of values.
	 */
	private class JdwpArrayregion {

		ArrayRegionPacket decode(byte[] bytes, int start) throws JdwpRuntimeException {
			ArrayRegionPacket arrayRegionPacket = new ArrayRegionPacket();
			int tag = decodeByte(bytes, start);
			int arrayLen = decodeInt(bytes, start + 1);
			int tagSize = valueTag.getSize(tag); // getSize(arrayLen, tag) - 1;
			boolean isPrim = valueTag.isPrimitive(tag);
			arrayRegionPacket.tag = tag;
			arrayRegionPacket.arrayLen = arrayLen;
			if (tagSize > 0 && arrayLen > 0) {
				arrayRegionPacket.idOrValues = new ArrayList<>(arrayLen);
				start += 5;
				for (int i = 0; i < arrayLen; i++) {
					if (!isPrim) {
						start++;
					}
					arrayRegionPacket.idOrValues.add(decodeBySize(bytes, start, tagSize));
					start += tagSize;
				}
			} else {
				arrayRegionPacket.idOrValues = Collections.emptyList();
			}
			return arrayRegionPacket;
		}

		void encode(ArrayRegionPacket val, ByteBuffer bytes) throws JdwpRuntimeException {
			JDWP.encodeByte(bytes, (byte) val.tag);
			JDWP.encodeInt(bytes, val.arrayLen);
			int tagSize = valueTag.getSize(val.tag);
			for (Long idOrVal : val.idOrValues) {
				encodeBySize(bytes, tagSize, idOrVal);
			}
		}

		public int getSize(int arrayLen, int tag) throws JdwpRuntimeException {
			return 5 + valueTag.getSize(tag) * arrayLen;
		}

		public class ArrayRegionPacket {
			public int tag;
			public int arrayLen;
			public List<Long> idOrValues;
		}
	}

	private class ValueTag {
		public int tag;
		public ByteBuffer idOrValue;
		int objectIDSize;

		public ValueTag() {
		}

		public ValueTag(int objectIDSize) {
			this.objectIDSize = objectIDSize;
		}

		public boolean isPrimitive(int tag) throws JdwpRuntimeException {
			switch (tag) {
				case Tag.BYTE:
				case Tag.CHAR:
				case Tag.FLOAT:
				case Tag.DOUBLE:
				case Tag.INT:
				case Tag.LONG:
				case Tag.SHORT:
				case Tag.VOID:
				case Tag.BOOLEAN:
					return true;
				case Tag.ARRAY:
				case Tag.OBJECT:
				case Tag.STRING:
				case Tag.THREAD:
				case Tag.THREAD_GROUP:
				case Tag.CLASS_LOADER:
				case Tag.CLASS_OBJECT:
					return false;
			}
			throw new JdwpRuntimeException("Unexpected tag: " + tag);
		}

		public int getSize(int tag) throws JdwpRuntimeException {
			switch (tag) {
				case Tag.ARRAY:
					return objectIDSize; // 91 '[' - an array object (objectID size).
				case Tag.BYTE:
					return 1; // 66 'B' - a byte value (1 byte).
				case Tag.CHAR:
					return 2; // 67 'C' - a character value (2 bytes).
				case Tag.OBJECT:
					return objectIDSize; // 76 'L' - an object (objectID size).
				case Tag.FLOAT:
					return 4; // 70 'F' - a float value (4 bytes).
				case Tag.DOUBLE:
					return 8; // 68 'D' - a double value (8 bytes).
				case Tag.INT:
					return 4; // 73 'I' - an int value (4 bytes).
				case Tag.LONG:
					return 8; // 74 'J' - a long value (8 bytes).
				case Tag.SHORT:
					return 2; // 83 'S' - a short value (2 bytes).
				case Tag.VOID:
					return 0; // 86 'V' - a void value (no bytes).
				case Tag.BOOLEAN:
					return 1; // 90 'Z' - a boolean value (1 byte).
				case Tag.STRING:
					return objectIDSize; // 115 's' - a String object (objectID size).
				case Tag.THREAD:
					return objectIDSize; // 116 't' - a Thread object (objectID size).
				case Tag.THREAD_GROUP:
					return objectIDSize; // 103 'g' - a ThreadGroup object (objectID size).
				case Tag.CLASS_LOADER:
					return objectIDSize; // 108 'l' - a ClassLoader object (objectID size).
				case Tag.CLASS_OBJECT:
					return objectIDSize; // 99 'c' - a class object object (objectID size).
			}
			throw new JdwpRuntimeException("Unexpected tag: " + tag);
		}

		public boolean getBoolean() throws JdwpRuntimeException {
			return decodeBoolean(idOrValue.getBytes(), 0);
		}

		public byte getByte() throws JdwpRuntimeException {
			return decodeByte(idOrValue.getBytes(), 0);
		}

		public char getChar() throws JdwpRuntimeException {
			return decodeChar(idOrValue.getBytes(), 0);
		}

		public short getShort() throws JdwpRuntimeException {
			return decodeShort(idOrValue.getBytes(), 0);
		}

		public int getInt() throws JdwpRuntimeException {
			return decodeInt(idOrValue.getBytes(), 0);
		}

		public float getFloat() throws JdwpRuntimeException {
			return decodeFloat(idOrValue.getBytes(), 0);
		}

		public double getDouble() throws JdwpRuntimeException {
			return decodeDouble(idOrValue.getBytes(), 0);
		}

		public long getLong() throws JdwpRuntimeException {
			return decodeBySize(idOrValue.getBytes(), 0, 8);
		}

		public long getID() throws JdwpRuntimeException {
			return mObjectID.decode(idOrValue.getBytes(), 0);
		}

		public ValueTag encode(int tag, Object idOrValue) throws JdwpRuntimeException {
			ValueTag valueTag = new ValueTag();
			valueTag.tag = tag;
			valueTag.idOrValue = new ByteBuffer();
			switch (tag) {
				case Tag.BYTE:
					JDWP.encodeByte(valueTag.idOrValue, (byte) idOrValue);
					return valueTag;
				case Tag.CHAR:
					JDWP.encodeChar(valueTag.idOrValue, (char) idOrValue);
					return valueTag;
				case Tag.FLOAT:
					encodeFloat(valueTag.idOrValue, (float) idOrValue);
					return valueTag;
				case Tag.DOUBLE:
					encodeDouble(valueTag.idOrValue, (double) idOrValue);
					return valueTag;
				case Tag.INT:
					JDWP.encodeInt(valueTag.idOrValue, (int) idOrValue);
					return valueTag;
				case Tag.LONG:
					encodeBySize(valueTag.idOrValue, 8, (long) idOrValue);
					return valueTag;
				case Tag.SHORT:
					JDWP.encodeShort(valueTag.idOrValue, (short) idOrValue);
					return valueTag;
				case Tag.VOID:
					return valueTag;
				case Tag.BOOLEAN:
					JDWP.encodeBoolean(valueTag.idOrValue, (boolean) idOrValue);
					return valueTag;
				case Tag.ARRAY:
				case Tag.OBJECT:
				case Tag.STRING:
				case Tag.THREAD:
				case Tag.THREAD_GROUP:
				case Tag.CLASS_LOADER:
				case Tag.CLASS_OBJECT:
					mObjectID.encode((long) idOrValue, valueTag.idOrValue);
					return valueTag;
			}
			throw new JdwpRuntimeException("Unexpected tag: " + tag);
		}
	}

	public static class JdwpRuntimeException extends RuntimeException {
		private static final long serialVersionUID = -1111111202103260221L;

		public JdwpRuntimeException(String msg) {
			super(msg);
		}
	}

	public static class ByteBuffer {
		private static final int MIN_INC = 16;
		private byte[] buf;
		private int cap;
		private int size;

		public ByteBuffer() {
			this(PACKET_HEADER_SIZE + 13);
		}

		public ByteBuffer(byte[] bytes) {
			this.setBuffer(bytes);
		}

		public ByteBuffer(int initCap) {
			buf = new byte[initCap];
			cap = initCap;
			size = 0;
		}

		private void extend(int incr) {
			cap += incr;
			byte[] tempBuf = new byte[cap];
			System.arraycopy(buf, 0, tempBuf, 0, size);
			buf = tempBuf;
		}

		public ByteBuffer set(int pos, byte b) {
			buf[pos] = b;
			return this;
		}

		public byte get(int pos) {
			return buf[pos];
		}

		public ByteBuffer setBuffer(byte[] bs) {
			buf = bs;
			size = bs.length;
			cap = bs.length;
			return this;
		}

		public ByteBuffer setPacketID(int id) {
			JDWP.setPacketID(buf, id);
			return this;
		}

		public void add(byte b) {
			if (size >= cap) {
				extend(MIN_INC);
			}
			buf[size] = b;
			size++;
		}

		public void addAll(byte[] bs) {
			int newSize = size + bs.length;
			if (newSize >= cap) {
				extend(Math.max(bs.length, MIN_INC));
			}
			System.arraycopy(bs, 0, buf, size, bs.length);
			size = newSize;
		}

		public void addAll(ByteBuffer bs) {
			int newSize = size + bs.size();
			if (newSize >= cap) {
				extend(Math.max(bs.size(), MIN_INC));
			}
			System.arraycopy(bs.buf, 0, buf, size, bs.size());
			size = newSize;
		}

		public int size() {
			return size;
		}

		public byte[] getBytes() {
			return getBytes(0);
		}

		public byte[] getBytes(int start) {
			int copySize = size - start;
			byte[] ret = new byte[copySize];
			System.arraycopy(buf, start, ret, 0, copySize);
			return ret;
		}

		public ByteBuffer resetIndex(int to) {
			size = to;
			return this;
		}

		public boolean equals(byte[] bs) {
			return Arrays.equals(buf, bs);
		}
	}
}
