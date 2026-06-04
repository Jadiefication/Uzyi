use crate::vm::VM;
use jni::errors::ThrowRuntimeExAndDefault;
use jni::objects::{JByteArray, JClass, JObject};
use jni::sys::jlong;
use jni::{jni_sig, jni_str, Env, EnvUnowned, JValue};

/// Builds a Kotlin/Java `io.jadie.VMState` instance from a native [`VM`] snapshot.
fn create_state<'caller>(vm: &VM, env: &mut Env<'caller>) -> jni::errors::Result<JObject<'caller>> {
    let registers = vm.get_registers();
    let j_registers = env.new_byte_array(registers.len())?;
    j_registers.set_region(env, 0, &registers)?;

    let memory = vm.memory.map(|it| it as i32);
    let j_memory = env.new_int_array(memory.len())?;
    j_memory.set_region(env, 0, &memory)?;

    let class = env.find_class(jni_str!("io/jadie/VMState"))?;
    let val = env.new_object(
        class,
        jni_sig!("([BIIZZI[I)V"),
        &[
            JValue::Object(&j_registers),
            JValue::Int(vm.counter as i32),
            JValue::Int(vm.status.collapse() as i32),
            JValue::Bool(vm.cf),
            JValue::Bool(vm.zf),
            JValue::Int(vm.cycles as i32),
            JValue::Object(&j_memory)
        ]
    )?;

    Ok(val)
}

/// JNI entry point to load and run bytecode in the Uzyi virtual machine.
///
/// # Arguments
/// * `opcodes` - A byte array containing the Uzyi bytecode.
///
/// # Returns
/// * A byte array containing the values of the 8 general-purpose registers after execution.
/**
 * JNI: `VMLoader.runVM(long vmPointer): VMState`
 *
 * Runs the VM until it halts (or yields due to sleep) and returns a fresh `VMState`.
 */
#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_io_jadie_VMLoader_runVM<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass,
    vmPointer: jlong,
) -> JObject<'caller> {
    unowned_env
        .with_env(|env| -> jni::errors::Result<JObject> {
            let raw_pointer = vmPointer as *mut VM;

            let vm: &mut VM = unsafe {
                assert!(!raw_pointer.is_null(), "Passed a null VM pointer from Kotlin!");
                &mut *raw_pointer
            };

            vm.run();

            return create_state(vm, env)
        }).resolve::<ThrowRuntimeExAndDefault>()
}

/**
 * JNI: `VMLoader.createVM(byte[] opcodes): long`
 *
 * Creates a native [`VM`] instance, loads up to 256 bytes of bytecode, and returns
 * an opaque pointer which must later be freed via `VMLoader.freeVM(long)`.
 */
#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_io_jadie_VMLoader_createVM<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass,
    opcodes: JByteArray,
) -> jlong {
    unowned_env
        .with_env(|env| -> jni::errors::Result<jlong> {
            if let Ok(byte_vector) = env.convert_byte_array(&opcodes) {
                let mut instructions = [0u8; 256];
                let len = byte_vector.len().min(256);
                instructions[..len].copy_from_slice(&byte_vector[..len]);

                let vm = VM::new(instructions);
                let box_vm = Box::new(vm);
                let raw: *mut VM = Box::into_raw(box_vm);

                return Ok(raw as i64)
            }
            panic!("Opcodes could not be converted!")
        }).resolve::<ThrowRuntimeExAndDefault>()
}

/**
 * JNI: `VMLoader.stepVM(long vmPointer): VMState`
 *
 * Executes a single instruction (if possible) and returns a `VMState` snapshot.
 */
#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_io_jadie_VMLoader_stepVM<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass,
    vmPointer: jlong,
) -> JObject<'caller> {
    unowned_env
        .with_env(|env| -> jni::errors::Result<JObject> {
            let raw_pointer = vmPointer as *mut VM;

            let vm: &mut VM = unsafe {
                assert!(!raw_pointer.is_null(), "Passed a null VM pointer from Kotlin!");
                &mut *raw_pointer
            };

            vm.step();

            return create_state(vm, env)
        }).resolve::<ThrowRuntimeExAndDefault>()
}

/**
 * JNI: `VMLoader.getState(long vmPointer): VMState`
 *
 * Returns a `VMState` snapshot without advancing the VM.
 */
#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_io_jadie_VMLoader_getState<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass,
    vmPointer: jlong,
) -> JObject<'caller> {
    unowned_env
        .with_env(|env| -> jni::errors::Result<JObject> {
            let raw_pointer = vmPointer as *mut VM;

            let vm: &mut VM = unsafe {
                assert!(!raw_pointer.is_null(), "Passed a null VM pointer from Kotlin!");
                &mut *raw_pointer
            };

            return create_state(vm, env)
        }).resolve::<ThrowRuntimeExAndDefault>()
}

/**
 * JNI: `VMLoader.freeVM(long vmPointer): void`
 *
 * Frees a previously created native [`VM`]. Safe to call with a null or already-freed pointer.
 */
#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_io_jadie_VMLoader_freeVM(
    _unowned_env: EnvUnowned,
    _class: JClass,
    vmPointer: jlong,
) {
    let raw_pointer = vmPointer as *mut VM;

    unsafe {
        if !raw_pointer.is_null() {
            let _boxed_vm = Box::from_raw(raw_pointer);
        }
    }
}