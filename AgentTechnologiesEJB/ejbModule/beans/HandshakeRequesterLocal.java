package beans;

import javax.ejb.Local;

@Local
public interface HandshakeRequesterLocal {

	public boolean sendRegisterRequest(String address, String alias);
}
