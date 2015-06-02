package clframework.common;

import static org.jocl.CL.CL_SUCCESS;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clGetProgramBuildInfo;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_program;

public class CLKernel {

	private CLContext clContext;
	private cl_program program = null;
	private Map<String, cl_kernel> kernels = new HashMap<String, cl_kernel>();

	public cl_program getProgram() {
		return program;
	}

	public cl_kernel getKernel(String name) {
		return kernels.get(name);
	}

	/**
	 * Releases resources used by the program and the kernel
	 */
	public void delete()
	{
		if (program != null) {
			clReleaseProgram(program);
			program = null;
		}
		
		for(cl_kernel kernel : kernels.values())
		{
			clReleaseKernel(kernel);
		}
		kernels.clear();
	}
	
	public CLKernel(CLContext clContext, String[] programSource, String[] kernelNames, 
			String compileOptions) throws Exception {
		this.clContext = clContext;
		
		int[] errcode = new int[1];
		program = clCreateProgramWithSource(clContext.getContext(), 1,
				programSource, null, errcode);

		if (errcode[0] != CL_SUCCESS) {
			throw new Exception(
					"Failed to create cl program from source! Error code: "
							+ errcode[0]);
		}

		cl_device_id[] devices = new cl_device_id[] { clContext.getClDevice().getClDevice() };

		int buildResult = clBuildProgram(program, 1, devices, compileOptions,
				null, null);

		if (buildResult != CL_SUCCESS) {
			long[] logSize = new long[1];
			clGetProgramBuildInfo(program, clContext.getClDevice().getClDevice(),
					CL.CL_PROGRAM_BUILD_LOG, 0, null, logSize);

			byte logData[] = new byte[(int) logSize[0]];
			clGetProgramBuildInfo(program, clContext.getClDevice().getClDevice(),
					CL.CL_PROGRAM_BUILD_LOG, logSize[0], Pointer.to(logData),
					null);
			throw new Exception("Failed to build cl program! Error code: "
					+ buildResult + " Build log: "
					+ System.getProperty("line.separator")
					+ new String(logData));
		}

		for (int i = 0; i < kernelNames.length; i++) {
			cl_kernel kernel = clCreateKernel(program, kernelNames[i], errcode);

			if (errcode[0] != CL_SUCCESS) {
				throw new Exception("Failed to create opencl kernel '" + kernelNames[i] + "'! Error code: "
						+ errcode[0]);
			}
			
			kernels.put(kernelNames[i], kernel);
		}
	}
	
	/**
	 * Enqueues a run of a kernel with automatic local work size.
	 * @param kernelName The kernel functions name to run
	 * @param params A list of memory objects to be set as kernel arguments 
	 * @param global_work_size The number of kernels to run in each dimension. The number of dimensions is specified via the length of this array
	 */
	public void enqueueNDRangeKernel(String kernelName, List<MemObject> params, long[] global_work_size)
	{
		for (int i = 0; i < params.size(); i++) {
			clSetKernelArg(kernels.get(kernelName), i, Sizeof.cl_mem,
					Pointer.to(params.get(i).getMemObject()));
		}
		
		clEnqueueNDRangeKernel(clContext.getCommandQueue(), kernels.get(kernelName),
				global_work_size.length, null, global_work_size, null, 0, null, null);
	}

	/**
	 * 
	 * @param kernelName The kernel functions name to run
	 * @param params A list of memory objects to be set as kernel arguments
	 * @param global_work_size The number of kernels to run in each dimension. The number of dimensions is specified via the length of this array
	 * @param global_work_offset Global work offset. If null is given, no offset will be applied
	 * @param local_work_size Local work size. If null, an automatic local work size will be calculated by the driver
	 */
	public void enqueueNDRangeKernel(String kernelName, List<MemObject> params, long[] global_work_size, long[] global_work_offset, long[] local_work_size)
	{
		for (int i = 0; i < params.size(); i++) {
			clSetKernelArg(kernels.get(kernelName), i, Sizeof.cl_mem,
					Pointer.to(params.get(i).getMemObject()));
		}
		
		clEnqueueNDRangeKernel(clContext.getCommandQueue(), kernels.get(kernelName),
				global_work_size.length, global_work_offset, global_work_size, local_work_size, 0, null, null);
	}
	
}
