use crate::vm::VM;
use jni::errors::ThrowRuntimeExAndDefault;
use jni::objects::{JByteArray, JClass};
use jni::EnvUnowned;

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_io_jadie_VMLoader_loadCodes<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass,
    opcodes: JByteArray,
) -> JByteArray<'caller> {
    unowned_env
        .with_env(|env| -> jni::errors::Result<JByteArray> {
            if let Ok(byte_vector) = env.convert_byte_array(&opcodes) {
                let mut instructions = [0u8; 256];
                let len = byte_vector.len().min(256);
                instructions[..len].copy_from_slice(&byte_vector[..len]);

                let mut vm = VM::new(instructions);
                vm.run();

                let registers = vm.get_registers();
                let output = env.new_byte_array(registers.len())?;
                output.set_region(env, 0, &registers)?;
                return Ok(output);
            }
            Ok(env.new_byte_array(0)?)
        }).resolve::<ThrowRuntimeExAndDefault>()
}