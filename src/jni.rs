use crate::vm::VM;
use jni::objects::{JByteArray, JClass, JObject};
use jni::EnvUnowned;

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_VmWrapper_loadCodes<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JClass,
    opcodes: JByteArray
) {
    let _ = unowned_env.with_env(|env| -> jni::errors::Result<_> {
        if let Ok(byte_vector) = env.convert_byte_array(&opcodes) {
            let mut vm = VM::new(*byte_vector.as_array().unwrap());
            vm.run();
        }
        Ok(JObject::null())
    });

}