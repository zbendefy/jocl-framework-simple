package clframework.common;

import static org.jocl.CL.*;

import org.jocl.Pointer;
import org.jocl.cl_mem;

public class MemObject {

	private cl_mem memobject = null;
	private CLContext context;
	private long memsize;

	private MemObject(CLContext c, long size, cl_mem mo) {
		memobject = mo;
		context = c;
		this.memsize = size;
	}

	cl_mem getMemObject() {
		return memobject;
	}

	public void delete() {
		if (memobject != null) {
			clReleaseMemObject(memobject);
			memobject = null;
		}
	}

	public void ReadBufferWithBlocking(Pointer destination) {
		clEnqueueReadBuffer(context.getCommandQueue(), memobject, CL_TRUE, 0,
				memsize, destination, 0, null, null);
	}

	public void ReadBufferWithBlocking(Pointer destination, long offset, long size) {
		clEnqueueReadBuffer(context.getCommandQueue(), memobject, CL_TRUE,
				offset, size, destination, 0, null, null);
	}
	
	public void enqueueReadBuffer(Pointer destination) {
		clEnqueueReadBuffer(context.getCommandQueue(), memobject, CL_FALSE, 0,
				memsize, destination, 0, null, null);
	}
	
	public void enqueueReadBuffer(Pointer destination, long offset, long size) {
		clEnqueueReadBuffer(context.getCommandQueue(), memobject, CL_FALSE,
				offset, size, destination, 0, null, null);
	}

	public static MemObject createMemObjectWriteOnly(CLContext context,
			long bytesize) throws Exception {
		int[] errcode = new int[1];
		cl_mem memobj = clCreateBuffer(context.getContext(), CL_MEM_WRITE_ONLY,
				bytesize, null, errcode);
		if (errcode[0] != CL_SUCCESS) {
			throw new Exception("Could not create memory object! Error code: "
					+ errcode[0]);
		}
		return new MemObject(context, bytesize, memobj);
	}

	public static MemObject createMemObjectReadWrite(CLContext context,
			long bytesize) throws Exception {
		int[] errcode = new int[1];
		cl_mem memobj = clCreateBuffer(context.getContext(), CL_MEM_READ_WRITE,
				bytesize, null, errcode);
		if (errcode[0] != CL_SUCCESS) {
			throw new Exception("Could not create memory object! Error code: "
					+ errcode[0]);
		}
		return new MemObject(context, bytesize, memobj);
	}

	public static MemObject createMemObjectReadOnly(CLContext context,
			long bytesize, Pointer initialData) throws Exception {
		int[] errcode = new int[1];
		cl_mem memobj = clCreateBuffer(context.getContext(), CL_MEM_READ_ONLY
				| CL_MEM_COPY_HOST_PTR, bytesize, initialData, errcode);
		if (errcode[0] != CL_SUCCESS) {
			throw new Exception("Could not create memory object! Error code: "
					+ errcode[0]);
		}
		return new MemObject(context, bytesize, memobj);
	}

	public static MemObject createMemObjectReadWrite(CLContext context,
			long bytesize, Pointer initialData) throws Exception {
		int[] errcode = new int[1];
		cl_mem memobj = clCreateBuffer(context.getContext(), CL_MEM_READ_WRITE
				| CL_MEM_COPY_HOST_PTR, bytesize, initialData, errcode);
		if (errcode[0] != CL_SUCCESS) {
			throw new Exception("Could not create memory object! Error code: "
					+ errcode[0]);
		}
		return new MemObject(context, bytesize, memobj);
	}

}
